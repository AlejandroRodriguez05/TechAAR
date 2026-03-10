// src/screens/ProfileScreen.tsx
import React, { useEffect } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import { useAuth } from '../context/AuthContext';

export default function ProfileScreen() {
  const { user, logout, refreshUser } = useAuth();

  // Refrescar datos del usuario al entrar
  useEffect(() => {
    refreshUser();
  }, []);

  const handleLogout = () => {
    Alert.alert(
      'Cerrar sesión',
      '¿Estás seguro de que quieres cerrar sesión?',
      [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Cerrar sesión', style: 'destructive', onPress: () => logout() },
      ]
    );
  };

  const getInitials = () => {
    if (!user) return '??';
    const n = user.nombre?.charAt(0) ?? '';
    const a = user.apellidos?.charAt(0) ?? '';
    return (n + a).toUpperCase() || '??';
  };

  const nombreCompleto = [user?.nombre, user?.apellidos].filter(Boolean).join(' ') || 'Sin nombre';

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.header}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{getInitials()}</Text>
          </View>
          <Text style={styles.nombre}>{nombreCompleto}</Text>
          <View style={styles.rolBadge}>
            <Text style={styles.rolText}>{user?.rol ?? 'USUARIO'}</Text>
          </View>
        </View>

        <View style={styles.infoSection}>
          <View style={styles.infoCard}>
            <View style={styles.infoRow}>
              <Ionicons name="mail-outline" size={20} color="rgba(255,255,255,0.7)" />
              <Text style={styles.infoText}>{user?.email || 'Sin email'}</Text>
            </View>
            <View style={styles.separator} />
            <View style={styles.infoRow}>
              <Ionicons name="card-outline" size={20} color="rgba(255,255,255,0.7)" />
              <Text style={styles.infoText}>{user?.nif || 'Sin NIF'}</Text>
            </View>
            <View style={styles.separator} />
            <View style={styles.infoRow}>
              <Ionicons name="people-outline" size={20} color="rgba(255,255,255,0.7)" />
              <Text style={styles.infoText}>
                {user?.rol === 'PROFESOR' ? 'Profesor/a' : 'Alumno/a'}
              </Text>
            </View>
            <View style={styles.separator} />
            <View style={styles.infoRow}>
              <Ionicons name="school-outline" size={20} color="rgba(255,255,255,0.7)" />
              <Text style={styles.infoText}>CIFP Villa de Agüimes</Text>
            </View>
          </View>
        </View>

        <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
          <Ionicons name="log-out-outline" size={22} color="#f43f5e" />
          <Text style={styles.logoutText}>Cerrar sesión</Text>
        </TouchableOpacity>

        <Text style={styles.version}>Versión 1.0.0</Text>
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  header: { alignItems: 'center', marginBottom: 30, marginTop: 20 },
  avatar: { width: 100, height: 100, borderRadius: 50, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center', marginBottom: 15, borderWidth: 2, borderColor: 'rgba(255,255,255,0.3)' },
  avatarText: { fontSize: 36, fontWeight: 'bold', color: '#fff' },
  nombre: { fontSize: 22, fontWeight: 'bold', color: '#fff', marginBottom: 8 },
  rolBadge: { backgroundColor: 'rgba(255,255,255,0.2)', paddingHorizontal: 16, paddingVertical: 6, borderRadius: 20 },
  rolText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  infoSection: { marginBottom: 30 },
  infoCard: { backgroundColor: 'rgba(255,255,255,0.1)', borderRadius: 12, padding: 20, borderWidth: 1, borderColor: 'rgba(255,255,255,0.15)' },
  infoRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 8 },
  infoText: { fontSize: 15, color: '#fff', marginLeft: 15 },
  separator: { height: 1, backgroundColor: 'rgba(255,255,255,0.1)', marginVertical: 4 },
  logoutButton: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: 'rgba(244,63,94,0.15)', paddingVertical: 14, borderRadius: 12, borderWidth: 1, borderColor: 'rgba(244,63,94,0.3)' },
  logoutText: { color: '#f43f5e', fontSize: 16, fontWeight: '600', marginLeft: 8 },
  version: { textAlign: 'center', color: 'rgba(255,255,255,0.4)', fontSize: 12, marginTop: 30 },
});
