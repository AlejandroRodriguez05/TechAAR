// src/context/AuthContext.tsx
import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { api, setToken, removeToken, getToken } from '../config/api';
import { Usuario } from '../types';

interface AuthState {
  user: Usuario | null;
  token: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
}

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

interface RegisterData {
  email: string;
  password: string;
  nif: string;
  nombre: string;
  apellidos: string;
  rol: 'PROFESOR' | 'ALUMNO';
  departamentoId?: number;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Extrae el token y el usuario de la respuesta del login.
 * Soporta múltiples formatos del backend:
 *   - { token, usuario: { id, nombre, ... } }
 *   - { token, id, nombre, apellidos, email, rol, nif }
 *   - { token, user: { id, nombre, ... } }
 */
function parseLoginResponse(data: any): { token: string; user: Usuario } {
  const token: string = data.token || data.accessToken || data.jwt || '';

  let user: Usuario;

  if (data.usuario && typeof data.usuario === 'object') {
    user = data.usuario as Usuario;
  } else if (data.user && typeof data.user === 'object') {
    user = data.user as Usuario;
  } else {
    // Los campos del usuario vienen a nivel raíz junto con el token
    user = {
      id: data.id ?? data.userId ?? 0,
      nif: data.nif ?? '',
      nombre: data.nombre ?? '',
      apellidos: data.apellidos ?? '',
      email: data.email ?? '',
      rol: data.rol ?? 'ALUMNO',
    };
  }

  return { token, user };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: null,
    isLoading: true,
    isAuthenticated: false,
  });

  useEffect(() => {
    checkStoredToken();
  }, []);

  const checkStoredToken = async () => {
    try {
      const storedToken = await getToken();
      if (storedToken) {
        const user = await api.get<Usuario>('/auth/me');
        setState({
          user,
          token: storedToken,
          isLoading: false,
          isAuthenticated: true,
        });
      } else {
        setState(prev => ({ ...prev, isLoading: false }));
      }
    } catch {
      await removeToken();
      setState({ user: null, token: null, isLoading: false, isAuthenticated: false });
    }
  };

  const login = useCallback(async (email: string, password: string) => {
    const response = await api.postPublic<any>('/auth/login', { email, password });

    console.log('[AuthContext] Login response keys:', Object.keys(response));

    const { token, user } = parseLoginResponse(response);

    if (!token) {
      throw new Error('No se recibió token del servidor');
    }

    await setToken(token);

    // Si el usuario viene incompleto del login, cargar desde /auth/me
    let fullUser = user;
    if (!fullUser.nombre || !fullUser.email) {
      try {
        // Ahora el token ya está guardado, api.get lo adjuntará
        fullUser = await api.get<Usuario>('/auth/me');
      } catch {
        // Usar lo que tengamos
      }
    }

    console.log('[AuthContext] User loaded:', JSON.stringify(fullUser));

    setState({
      user: fullUser,
      token,
      isLoading: false,
      isAuthenticated: true,
    });
  }, []);

  const register = useCallback(async (data: RegisterData) => {
    const response = await api.postPublic<any>('/auth/register', data);
    const { token, user } = parseLoginResponse(response);

    if (token) await setToken(token);

    setState({
      user,
      token,
      isLoading: false,
      isAuthenticated: true,
    });
  }, []);

  const logout = useCallback(async () => {
    await removeToken();
    setState({ user: null, token: null, isLoading: false, isAuthenticated: false });
  }, []);

  const refreshUser = useCallback(async () => {
    try {
      const user = await api.get<Usuario>('/auth/me');
      setState(prev => ({ ...prev, user }));
    } catch (error: any) {
      // Solo hacer logout si el token es inválido/expirado (401)
      if (error?.status === 401) {
        await logout();
      }
      // Para otros errores (red, 500, etc.) mantener datos en memoria
    }
  }, [logout]);

  return (
    <AuthContext.Provider value={{ ...state, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return context;
}
