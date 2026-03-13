// src/screens/EmpresaDetailScreen.tsx
import React, { useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  Linking, Modal, TextInput, Alert, ActivityIndicator,
} from 'react-native';
import { RouteProp, useRoute, useNavigation, useFocusEffect } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import { useAuth } from '../context/AuthContext';
import { empresaService } from '../services/empresaService';
import { comentarioService } from '../services/comentarioService';
import { valoracionService } from '../services/valoracionService';
import { plazaService, PlazaResponse } from '../services/plazaService';
import { contactadaService, EmpresaContactadaResponse } from '../services/contactadaService';
import { reservaService } from '../services/reservaService';
import { favoritoService } from '../services/favoritoService';
import { departamentoService } from '../services/departamentoService';
import { listaService } from '../services/listaService';
import { Empresa, Comentario, Curso, Departamento, Lista, ReservaPlaza } from '../types';
import { RootStackParamList } from '../navigation/AppNavigator';

type EmpresaDetailRouteProp = RouteProp<RootStackParamList, 'EmpresaDetail'>;
type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function EmpresaDetailScreen() {
  const route = useRoute<EmpresaDetailRouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { user } = useAuth();
  const { empresaId } = route.params;
  const esProfesor = user?.rol === 'PROFESOR';

  const [empresa, setEmpresa] = useState<Empresa | null>(null);
  const [comentarios, setComentarios] = useState<Comentario[]>([]);
  const [plazas, setPlazas] = useState<PlazaResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const [esFavorito, setEsFavorito] = useState(false);
  const [tabComentarios, setTabComentarios] = useState<'general' | 'profesores'>('general');

  // Nuevo comentario
  const [nuevoComentario, setNuevoComentario] = useState('');
  const [enviandoComentario, setEnviandoComentario] = useState(false);

  // Modals
  const [modalReservaVisible, setModalReservaVisible] = useState(false);
  const [cantidadReserva, setCantidadReserva] = useState(1);
  const [claseReserva, setClaseReserva] = useState('');
  const [cursoSeleccionado, setCursoSeleccionado] = useState<number | null>(null);
  const [plazaIdActual, setPlazaIdActual] = useState<number>(0);
  const [cursosDepto, setCursosDepto] = useState<Curso[]>([]);

  const [modalValorarVisible, setModalValorarVisible] = useState(false);
  const [valoracionSeleccionada, setValoracionSeleccionada] = useState(0);

  const [modalContactadoVisible, setModalContactadoVisible] = useState(false);
  const [deptoContactado, setDeptoContactado] = useState<number | null>(null);
  const [departamentos, setDepartamentos] = useState<Departamento[]>([]);

  // Contactadas y reservas con ID para poder eliminar
  const [contactadas, setContactadas] = useState<EmpresaContactadaResponse[]>([]);
  const [reservas, setReservas] = useState<ReservaPlaza[]>([]);

  // Lista
  const [modalListaVisible, setModalListaVisible] = useState(false);
  const [listas, setListas] = useState<Lista[]>([]);
  const [loadingListas, setLoadingListas] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      // Empresa siempre se carga
      const emp = await empresaService.getById(empresaId);
      setEmpresa(emp);

      // Los demás endpoints pueden fallar (permisos) - cargar con catch individual
      const [coms, plz] = await Promise.all([
        comentarioService.getByEmpresa(empresaId).catch(() => []),
        plazaService.getByEmpresa(empresaId).catch(() => []),
      ]);
      setComentarios(coms ?? []);
      setPlazas(plz ?? []);

      // Check favorito
      try {
        const fav = await favoritoService.isFavorito(empresaId);
        setEsFavorito(fav.esFavorita);
      } catch { /* ignore */ }

      // Cargar contactadas y reservas con sus IDs para poder eliminar
      const [ctds, rvs] = await Promise.all([
        contactadaService.getByEmpresa(empresaId).catch(() => []),
        reservaService.getByEmpresa(empresaId).catch(() => []),
      ]);
      setContactadas(ctds ?? []);
      setReservas(rvs ?? []);
    } catch (error) {
      console.error('Error cargando empresa:', error);
    } finally {
      setLoading(false);
    }
  }, [empresaId]);

  useFocusEffect(useCallback(() => { fetchData(); }, [fetchData]));

  if (loading) {
    return (
      <GradientBackground>
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </GradientBackground>
    );
  }

  if (!empresa) {
    return (
      <GradientBackground>
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 }}>
          <Ionicons name="alert-circle-outline" size={60} color="rgba(255,255,255,0.5)" />
          <Text style={{ color: '#fff', fontSize: 16, marginTop: 15, textAlign: 'center' }}>
            No se pudo cargar la empresa
          </Text>
        </View>
      </GradientBackground>
    );
  }

  const comentariosGenerales = comentarios.filter(c => !c.esPrivado);
  const comentariosProfesores = comentarios.filter(c => c.esPrivado);
  const comentariosMostrados = tabComentarios === 'general' ? comentariosGenerales : comentariosProfesores;

  const handleCall = () => { if (empresa.telefono) Linking.openURL(`tel:${empresa.telefono}`); };
  const handleEmail = () => { if (empresa.email) Linking.openURL(`mailto:${empresa.email}`); };
  const handleEdit = () => { navigation.navigate('EditEmpresa', { empresaId: empresa.id }); };

  // --- Enviar comentario ---
  const handleEnviarComentario = async () => {
    if (!nuevoComentario.trim()) return;
    setEnviandoComentario(true);
    try {
      await comentarioService.crear({
        empresaId,
        texto: nuevoComentario.trim(),
        esPrivado: tabComentarios === 'profesores',
      });
      setNuevoComentario('');
      // Recargar comentarios
      const coms = await comentarioService.getByEmpresa(empresaId).catch(() => []);
      setComentarios(coms ?? []);
    } catch (error) {
      Alert.alert('Error', 'No se pudo enviar el comentario');
    } finally {
      setEnviandoComentario(false);
    }
  };

  // --- Eliminar comentario (solo el propio) ---
  const handleEliminarComentario = (id: number) => {
    Alert.alert('Eliminar comentario', '¿Deseas eliminar este comentario?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            await comentarioService.eliminar(id);
            const coms = await comentarioService.getByEmpresa(empresaId).catch(() => []);
            setComentarios(coms ?? []);
          } catch {
            Alert.alert('Error', 'No se pudo eliminar el comentario');
          }
        },
      },
    ]);
  };

  // --- Eliminar plaza ---
  const handleEliminarPlaza = (id: number) => {
    Alert.alert('Eliminar plaza', '¿Deseas eliminar esta plaza? Se eliminarán también sus reservas asociadas.', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            await plazaService.eliminar(id);
            fetchData();
          } catch {
            Alert.alert('Error', 'No se pudo eliminar la plaza');
          }
        },
      },
    ]);
  };

  // --- Reservar ---
  const abrirModalReserva = async (plazaId: number, departamentoId: number) => {
    setPlazaIdActual(plazaId);
    setCursoSeleccionado(null);
    setCantidadReserva(1);
    setClaseReserva('');

    // Buscar la plaza para saber si es general o de un curso específico
    const plaza = plazas.find(p => p.id === plazaId);
    if (plaza && !plaza.esGeneral && plaza.cursoId) {
      // Plaza de curso específico: solo mostrar ese curso
      setCursosDepto([{
        id: plaza.cursoId,
        nombre: plaza.cursoNombre ?? '',
        siglas: plaza.cursoSiglas ?? '',
        departamentoId,
        grado: 'SUPERIOR' as const,
      }]);
      setCursoSeleccionado(plaza.cursoId);
    } else {
      // Plaza general: mostrar todos los cursos del departamento
      try {
        const cursos = await departamentoService.getCursos(departamentoId);
        setCursosDepto(cursos ?? []);
      } catch { setCursosDepto([]); }
    }
    setModalReservaVisible(true);
  };

  const handleReservarPlazas = async () => {
    if (!cursoSeleccionado) {
      Alert.alert('Error', 'Debes seleccionar un ciclo formativo');
      return;
    }
    const cursoInfo = cursosDepto.find(c => c.id === cursoSeleccionado);
    try {
      await reservaService.crear({
        plazaId: plazaIdActual,
        cursoId: cursoSeleccionado,
        cantidad: cantidadReserva,
        clase: claseReserva || undefined,
      });
      setModalReservaVisible(false);
      Alert.alert('Plazas reservadas', `Has reservado ${cantidadReserva} plaza${cantidadReserva > 1 ? 's' : ''} para ${cursoInfo?.siglas}`);
      fetchData();
    } catch (error) {
      Alert.alert('Error', 'No se pudieron reservar las plazas');
    }
  };

  // --- Contactado ---
  const abrirModalContactado = async () => {
    setDeptoContactado(null);
    try {
      const deps = await departamentoService.getAll();
      setDepartamentos(deps ?? []);
    } catch { /* ignore */ }
    setModalContactadoVisible(true);
  };

  const handleMarcarContactado = async () => {
    if (!deptoContactado) {
      Alert.alert('Error', 'Debes seleccionar un departamento');
      return;
    }
    try {
      await contactadaService.crear({
        empresaId, departamentoId: deptoContactado,
      });
      setModalContactadoVisible(false);
      Alert.alert('Empresa contactada', 'Se ha marcado la empresa como contactada');
      fetchData();
    } catch (error) {
      Alert.alert('Error', 'No se pudo marcar como contactado');
    }
  };

  // --- Eliminar contactado (solo el propio) ---
  const handleEliminarContactado = (id: number) => {
    Alert.alert('Eliminar contactado', '¿Deseas eliminar este registro de contacto?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            await contactadaService.eliminar(id);
            fetchData();
          } catch {
            Alert.alert('Error', 'No se pudo eliminar el contacto');
          }
        },
      },
    ]);
  };

  // --- Eliminar reserva (solo la propia) ---
  const handleEliminarReserva = (id: number) => {
    Alert.alert('Eliminar reserva', '¿Deseas eliminar esta reserva?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            await reservaService.eliminar(id);
            fetchData();
          } catch {
            Alert.alert('Error', 'No se pudo eliminar la reserva');
          }
        },
      },
    ]);
  };

  // --- Valorar ---
  const handleValorar = async () => {
    if (valoracionSeleccionada === 0) {
      Alert.alert('Error', 'Debes seleccionar una valoración');
      return;
    }
    try {
      await valoracionService.crear({ empresaId, puntuacion: valoracionSeleccionada });
      setModalValorarVisible(false);
      Alert.alert('¡Gracias!', `Has valorado con ${valoracionSeleccionada} estrella${valoracionSeleccionada > 1 ? 's' : ''}`);
      setValoracionSeleccionada(0);
      fetchData();
    } catch (error) {
      Alert.alert('Error', 'No se pudo enviar la valoración');
    }
  };

  // --- Favorito ---
  const toggleFavorito = async () => {
    try {
      const result = await favoritoService.toggle(empresaId);
      const nuevoEstado = result.esFavorita;
      setEsFavorito(nuevoEstado);
      Alert.alert(nuevoEstado ? 'Añadido a favoritos ❤️' : 'Eliminado de favoritos');
    } catch (error) {
      // Si el endpoint no devuelve { favorito: bool }, alternar localmente
      setEsFavorito(!esFavorito);
      Alert.alert(!esFavorito ? 'Añadido a favoritos ❤️' : 'Eliminado de favoritos');
    }
  };

  // --- Agregar a lista ---
  const abrirModalLista = async () => {
    setLoadingListas(true);
    setModalListaVisible(true);
    try {
      const data = await listaService.getMisListas();
      setListas(data ?? []);
    } catch { setListas([]); }
    setLoadingListas(false);
  };

  const handleAgregarALista = async (listaId: number) => {
    try {
      await listaService.addEmpresa(listaId, empresaId);
      setModalListaVisible(false);
      Alert.alert('Añadida', 'Empresa añadida a la lista');
    } catch (error) {
      Alert.alert('Error', 'No se pudo añadir a la lista');
    }
  };

  return (
    <GradientBackground>
      <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
        {/* Cabecera */}
        <View style={styles.header}>
          <Text style={styles.nombre}>{empresa.nombre}</Text>
          {empresa.valoracionMedia != null && empresa.valoracionMedia > 0 && (
            <View style={styles.valoracionContainer}>
              <View style={styles.estrellas}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <Ionicons key={star} name={star <= Math.round(empresa.valoracionMedia!) ? 'star' : 'star-outline'} size={20} color="#fbbf24" />
                ))}
              </View>
              <Text style={styles.valoracionTexto}>
                {empresa.valoracionMedia.toFixed(1)} ({empresa.totalValoraciones ?? 0} val.)
              </Text>
            </View>
          )}
          <View style={styles.tags}>
            {(empresa.departamentos ?? []).map((dept) => (
              <View key={dept.id} style={styles.tag}>
                <Text style={styles.tagText}>{dept.codigo ?? dept.nombre}</Text>
              </View>
            ))}
          </View>
        </View>

        {/* Plazas disponibles */}
        {plazas.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>📊 Plazas disponibles</Text>
            {plazas.map((info, index) => (
              <View key={index} style={styles.plazasCard}>
                <View style={styles.plazasHeader}>
                  <View style={{ flex: 1 }}>
                    <Text style={styles.plazasDepto}>{info.departamentoNombre}</Text>
                    {info.esGeneral ? (
                      <View style={styles.generalBadge}>
                        <Ionicons name="people" size={12} color="#0891b2" />
                        <Text style={styles.generalText}>General · {info.cantidad} plaza{info.cantidad !== 1 ? 's' : ''}</Text>
                      </View>
                    ) : (
                      <View style={styles.generalBadge}>
                        <Ionicons name="school-outline" size={12} color="#7c3aed" />
                        <Text style={[styles.generalText, { color: '#7c3aed' }]}>
                          {info.cursoSiglas ?? 'Ciclo específico'}{info.cursoNombre ? ` · ${info.cursoNombre}` : ''} · {info.cantidad} plaza{info.cantidad !== 1 ? 's' : ''}
                        </Text>
                      </View>
                    )}
                  </View>
                  {esProfesor && info.creadorId === user?.id && (
                    <TouchableOpacity onPress={() => handleEliminarPlaza(info.id)} style={{ padding: 5 }}>
                      <Ionicons name="trash-outline" size={18} color="#ef4444" />
                    </TouchableOpacity>
                  )}
                </View>
                <View style={styles.plazasInfo}>
                  <View style={styles.plazasItem}><Text style={styles.plazasNumero}>{info.cantidad}</Text><Text style={styles.plazasLabel}>Ofertadas</Text></View>
                  <View style={styles.plazasDivider} />
                  <View style={styles.plazasItem}><Text style={styles.plazasNumero}>{info.plazasReservadas}</Text><Text style={styles.plazasLabel}>Reservadas</Text></View>
                  <View style={styles.plazasDivider} />
                  <View style={styles.plazasItem}><Text style={[styles.plazasNumero, { color: info.plazasDisponibles > 0 ? '#10b981' : '#ef4444' }]}>{info.plazasDisponibles}</Text><Text style={styles.plazasLabel}>Libres</Text></View>
                </View>
                {esProfesor && info.plazasDisponibles > 0 && (
                  <TouchableOpacity style={styles.reservarBtn} onPress={() => abrirModalReserva(info.id, info.departamentoId)}>
                    <Ionicons name="add-circle-outline" size={18} color="#0891b2" />
                    <Text style={styles.reservarBtnText}>Reservar plazas</Text>
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </View>
        )}

        {/* Reservas */}
        {esProfesor && reservas.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Reservas</Text>
            {reservas.map((r) => (
              <View key={r.id} style={styles.reservaItem}>
                <View style={{ flex: 1 }}>
                  <Text style={styles.reservaProfesor}>{r.profesorNombre}</Text>
                  <Text style={styles.reservaDetalle}>
                    {r.cursoSiglas ?? 'General'} · {r.cantidad} plaza{r.cantidad !== 1 ? 's' : ''}
                    {r.clase ? ` · Clase ${r.clase}` : ''}
                  </Text>
                </View>
                {r.profesorId === user?.id && (
                  <TouchableOpacity onPress={() => handleEliminarReserva(r.id)} style={{ padding: 5 }}>
                    <Ionicons name="trash-outline" size={18} color="#ef4444" />
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </View>
        )}

        {/* Contactado por */}
        {contactadas.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Contactado por</Text>
            {contactadas.map((c) => (
              <View key={c.id} style={styles.contactadoItem}>
                <Ionicons name="person-circle-outline" size={22} color="#059669" />
                <View style={{ marginLeft: 10, flex: 1 }}>
                  <Text style={styles.contactadoProfesor}>{c.profesorNombre}</Text>
                  <Text style={styles.contactadoDepto}>{c.departamentoNombre} · {c.fecha}</Text>
                </View>
                {c.profesorId === user?.id && (
                  <TouchableOpacity onPress={() => handleEliminarContactado(c.id)} style={{ padding: 5 }}>
                    <Ionicons name="trash-outline" size={18} color="#ef4444" />
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </View>
        )}

        {/* Contacto */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Información de contacto</Text>
          {empresa.direccion && (
            <View style={styles.infoRow}>
              <Ionicons name="location-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{empresa.direccion}, {empresa.ciudad}</Text>
            </View>
          )}
          {!empresa.direccion && empresa.ciudad && (
            <View style={styles.infoRow}>
              <Ionicons name="location-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{empresa.ciudad}</Text>
            </View>
          )}
          {empresa.telefono && (
            <TouchableOpacity style={styles.infoRow} onPress={handleCall}>
              <Ionicons name="call-outline" size={20} color="#0891b2" />
              <Text style={[styles.infoText, styles.link]}>{empresa.telefono}</Text>
            </TouchableOpacity>
          )}
          {empresa.email && (
            <TouchableOpacity style={styles.infoRow} onPress={handleEmail}>
              <Ionicons name="mail-outline" size={20} color="#0891b2" />
              <Text style={[styles.infoText, styles.link]}>{empresa.email}</Text>
            </TouchableOpacity>
          )}
          {empresa.personaContacto && (
            <View style={styles.infoRow}>
              <Ionicons name="person-outline" size={20} color="#666" />
              <Text style={styles.infoText}>Contacto: {empresa.personaContacto}{empresa.telefonoContacto ? ` (${empresa.telefonoContacto})` : ''}</Text>
            </View>
          )}
        </View>

        {/* Descripción */}
        {empresa.descripcion ? (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Descripción</Text>
            <Text style={styles.descripcion}>{empresa.descripcion}</Text>
          </View>
        ) : null}

        {/* Cursos */}
        {empresa.cursos && empresa.cursos.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Ciclos formativos</Text>
            {empresa.cursos.map((curso) => (
              <View key={curso.id} style={styles.cursoItem}>
                <Text style={styles.cursoSiglas}>{curso.siglas}</Text>
                <Text style={styles.cursoNombre}>{curso.nombre}</Text>
              </View>
            ))}
          </View>
        )}

        {/* Comentarios */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Comentarios</Text>

          {/* Tabs: General + Profesores */}
          <View style={styles.comentariosTabs}>
            <TouchableOpacity style={[styles.comentariosTab, tabComentarios === 'general' && styles.comentariosTabActive]} onPress={() => setTabComentarios('general')}>
              <Text style={[styles.comentariosTabText, tabComentarios === 'general' && styles.comentariosTabTextActive]}>General ({comentariosGenerales.length})</Text>
            </TouchableOpacity>
            {esProfesor && (
              <TouchableOpacity style={[styles.comentariosTab, tabComentarios === 'profesores' && styles.comentariosTabActive]} onPress={() => setTabComentarios('profesores')}>
                <Text style={[styles.comentariosTabText, tabComentarios === 'profesores' && styles.comentariosTabTextActive]}>Profesores ({comentariosProfesores.length})</Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Lista de comentarios */}
          {comentariosMostrados.length > 0 ? (
            comentariosMostrados.map((c) => (
              <View key={c.id} style={styles.comentarioCard}>
                <View style={styles.comentarioHeaderRow}>
                  <Text style={styles.comentarioAutor}>{c.usuarioNombre}</Text>
                  <Text style={styles.comentarioFecha}>{c.fecha}</Text>
                  {c.usuarioId === user?.id && (
                    <TouchableOpacity onPress={() => handleEliminarComentario(c.id)} style={{ padding: 4 }}>
                      <Ionicons name="trash-outline" size={16} color="#ef4444" />
                    </TouchableOpacity>
                  )}
                </View>
                <Text style={styles.comentarioTexto}>{c.texto}</Text>
              </View>
            ))
          ) : (
            <View style={styles.comentariosEmpty}>
              <Ionicons name="chatbubble-outline" size={40} color="#ccc" />
              <Text style={styles.comentariosEmptyText}>No hay comentarios aún</Text>
            </View>
          )}

          {/* Input para escribir comentario */}
          <View style={styles.comentarioInputContainer}>
            <TextInput
              style={styles.comentarioInput}
              placeholder="Escribe un comentario..."
              placeholderTextColor="#999"
              value={nuevoComentario}
              onChangeText={setNuevoComentario}
              multiline
              maxLength={500}
            />
            <View style={styles.comentarioActions}>
              <TouchableOpacity
                style={[styles.enviarBtn, !nuevoComentario.trim() && styles.enviarBtnDisabled]}
                onPress={handleEnviarComentario}
                disabled={!nuevoComentario.trim() || enviandoComentario}
              >
                {enviandoComentario ? (
                  <ActivityIndicator size="small" color="#fff" />
                ) : (
                  <Ionicons name="send" size={18} color="#fff" />
                )}
              </TouchableOpacity>
            </View>
          </View>
        </View>

        {/* Acciones */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Acciones</Text>
          <View style={styles.accionesGrid}>
            <TouchableOpacity style={[styles.accionCard, esFavorito && styles.accionCardActive]} onPress={toggleFavorito}>
              <Ionicons name={esFavorito ? "heart" : "heart-outline"} size={24} color="#f43f5e" />
              <Text style={styles.accionText}>{esFavorito ? 'En favoritos' : 'Favorito'}</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.accionCard} onPress={() => { setValoracionSeleccionada(0); setModalValorarVisible(true); }}>
              <Ionicons name="star-outline" size={24} color="#f59e0b" />
              <Text style={styles.accionText}>Valorar</Text>
            </TouchableOpacity>
          </View>
          <TouchableOpacity style={[styles.accionCard, { marginTop: 12 }]} onPress={abrirModalLista}>
            <Ionicons name="list-outline" size={24} color="#6366f1" />
            <Text style={styles.accionText}>Agregar a lista</Text>
          </TouchableOpacity>
        </View>

        {/* Acciones de profesor */}
        {esProfesor && (
          <View style={styles.profesorActions}>
            <TouchableOpacity style={styles.actionButtonSecondary} onPress={handleEdit}>
              <Ionicons name="create-outline" size={22} color="#0891b2" />
              <Text style={styles.actionTextWhite}>Editar información</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButtonPrimary} onPress={abrirModalContactado}>
              <Ionicons name="checkmark-circle-outline" size={22} color="#059669" />
              <Text style={styles.actionTextGreen}>Marcar contactado</Text>
            </TouchableOpacity>
          </View>
        )}

        <View style={{ height: 30 }} />
      </ScrollView>

      {/* Modal reservar plazas */}
      <Modal animationType="fade" transparent visible={modalReservaVisible} onRequestClose={() => setModalReservaVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Reservar plazas</Text>
              <TouchableOpacity onPress={() => setModalReservaVisible(false)}><Ionicons name="close" size={24} color="#666" /></TouchableOpacity>
            </View>
            <Text style={styles.modalLabel}>Ciclo formativo *</Text>
            <ScrollView style={{ maxHeight: 150 }} nestedScrollEnabled>
              <View style={{ gap: 8 }}>
                {cursosDepto.map((curso) => (
                  <TouchableOpacity key={curso.id} style={[styles.optionItem, cursoSeleccionado === curso.id && styles.optionItemSelected]} onPress={() => setCursoSeleccionado(curso.id)}>
                    <Text style={[styles.optionText, cursoSeleccionado === curso.id && styles.optionTextSelected]}>{curso.siglas} - {curso.nombre}</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </ScrollView>
            <Text style={styles.modalLabel}>Cantidad</Text>
            <View style={styles.cantidadContainer}>
              <TouchableOpacity style={styles.cantidadBtn} onPress={() => setCantidadReserva(Math.max(1, cantidadReserva - 1))}><Ionicons name="remove" size={20} color="#059669" /></TouchableOpacity>
              <Text style={styles.cantidadText}>{cantidadReserva}</Text>
              <TouchableOpacity style={styles.cantidadBtn} onPress={() => setCantidadReserva(cantidadReserva + 1)}><Ionicons name="add" size={20} color="#059669" /></TouchableOpacity>
            </View>
            <Text style={styles.modalLabel}>Clase (opcional)</Text>
            <TextInput style={styles.modalInput} placeholder="Ej: 2A" placeholderTextColor="#999" value={claseReserva} onChangeText={setClaseReserva} maxLength={5} />
            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalReservaVisible(false)}><Text style={styles.modalButtonCancelText}>Cancelar</Text></TouchableOpacity>
              <TouchableOpacity style={[styles.modalButtonConfirm, !cursoSeleccionado && styles.modalButtonDisabled]} onPress={handleReservarPlazas} disabled={!cursoSeleccionado}><Text style={styles.modalButtonConfirmText}>Reservar</Text></TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal contactado */}
      <Modal animationType="fade" transparent visible={modalContactadoVisible} onRequestClose={() => setModalContactadoVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Marcar como contactado</Text>
              <TouchableOpacity onPress={() => setModalContactadoVisible(false)}><Ionicons name="close" size={24} color="#666" /></TouchableOpacity>
            </View>
            <Text style={styles.modalLabel}>¿Por qué departamento?</Text>
            <View style={{ gap: 8 }}>
              {departamentos.map((d) => (
                <TouchableOpacity key={d.id} style={[styles.optionItem, deptoContactado === d.id && styles.optionItemSelected]} onPress={() => setDeptoContactado(d.id)}>
                  <Text style={[styles.optionText, deptoContactado === d.id && styles.optionTextSelected]}>{d.codigo} - {d.nombre}</Text>
                </TouchableOpacity>
              ))}
            </View>
            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalContactadoVisible(false)}><Text style={styles.modalButtonCancelText}>Cancelar</Text></TouchableOpacity>
              <TouchableOpacity style={[styles.modalButtonConfirm, !deptoContactado && styles.modalButtonDisabled]} onPress={handleMarcarContactado} disabled={!deptoContactado}><Text style={styles.modalButtonConfirmText}>Confirmar</Text></TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal valorar */}
      <Modal animationType="fade" transparent visible={modalValorarVisible} onRequestClose={() => setModalValorarVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Valorar empresa</Text>
              <TouchableOpacity onPress={() => setModalValorarVisible(false)}><Ionicons name="close" size={24} color="#666" /></TouchableOpacity>
            </View>
            <View style={styles.estrellasContainer}>
              {[1, 2, 3, 4, 5].map((star) => (
                <TouchableOpacity key={star} onPress={() => setValoracionSeleccionada(star)} style={{ padding: 5 }}>
                  <Ionicons name={star <= valoracionSeleccionada ? 'star' : 'star-outline'} size={40} color={star <= valoracionSeleccionada ? '#f59e0b' : '#d1d5db'} />
                </TouchableOpacity>
              ))}
            </View>
            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalValorarVisible(false)}><Text style={styles.modalButtonCancelText}>Cancelar</Text></TouchableOpacity>
              <TouchableOpacity style={[styles.modalButtonConfirm, valoracionSeleccionada === 0 && styles.modalButtonDisabled]} onPress={handleValorar} disabled={valoracionSeleccionada === 0}><Text style={styles.modalButtonConfirmText}>Valorar</Text></TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal agregar a lista */}
      <Modal animationType="fade" transparent visible={modalListaVisible} onRequestClose={() => setModalListaVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Agregar a lista</Text>
              <TouchableOpacity onPress={() => setModalListaVisible(false)}><Ionicons name="close" size={24} color="#666" /></TouchableOpacity>
            </View>
            {loadingListas ? (
              <ActivityIndicator size="large" color="#6366f1" style={{ marginVertical: 20 }} />
            ) : listas.length > 0 ? (
              <ScrollView style={{ maxHeight: 250 }} nestedScrollEnabled>
                <View style={{ gap: 8 }}>
                  {listas.map((lista) => (
                    <TouchableOpacity key={lista.id} style={styles.optionItem} onPress={() => handleAgregarALista(lista.id)}>
                      <Text style={styles.optionText}>{lista.nombre}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </ScrollView>
            ) : (
              <View style={{ alignItems: 'center', paddingVertical: 20 }}>
                <Ionicons name="list-outline" size={40} color="#ccc" />
                <Text style={{ color: '#999', marginTop: 10 }}>No tienes listas creadas</Text>
              </View>
            )}
            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalListaVisible(false)}><Text style={styles.modalButtonCancelText}>Cerrar</Text></TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  header: { padding: 20 }, nombre: { fontSize: 24, fontWeight: 'bold', color: '#fff', marginBottom: 10 },
  valoracionContainer: { flexDirection: 'row', alignItems: 'center', marginBottom: 15 }, estrellas: { flexDirection: 'row' },
  valoracionTexto: { marginLeft: 10, fontSize: 14, color: 'rgba(255,255,255,0.8)' },
  tags: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 }, tag: { backgroundColor: 'rgba(255,255,255,0.2)', paddingHorizontal: 12, paddingVertical: 5, borderRadius: 8 }, tagText: { color: '#fff', fontSize: 12, fontWeight: '500' },
  section: { backgroundColor: '#fff', marginHorizontal: 15, marginBottom: 15, borderRadius: 12, padding: 15, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#333', marginBottom: 15 },
  plazasCard: { backgroundColor: '#f8fafc', borderRadius: 10, padding: 15, marginBottom: 10, borderWidth: 1, borderColor: '#e2e8f0' },
  plazasHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 },
  plazasDepto: { fontSize: 15, fontWeight: '600', color: '#333' },
  generalBadge: { flexDirection: 'row', alignItems: 'center', marginTop: 4 }, generalText: { fontSize: 12, color: '#0891b2', marginLeft: 4, fontWeight: '500' },
  plazasBadge: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 }, plazasBadgeDisponible: { backgroundColor: '#d1fae5' }, plazasBadgeLleno: { backgroundColor: '#fee2e2' },
  plazasBadgeText: { fontSize: 12, fontWeight: '600' }, plazasBadgeTextVerde: { color: '#065f46' }, plazasBadgeTextRojo: { color: '#991b1b' },
  plazasInfo: { flexDirection: 'row', justifyContent: 'space-around', alignItems: 'center' },
  plazasItem: { alignItems: 'center' }, plazasNumero: { fontSize: 24, fontWeight: 'bold', color: '#333' }, plazasLabel: { fontSize: 12, color: '#666', marginTop: 2 },
  plazasDivider: { width: 1, height: 40, backgroundColor: '#e2e8f0' },
  reservarBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', marginTop: 12, paddingTop: 12, borderTopWidth: 1, borderTopColor: '#e2e8f0' },
  reservarBtnText: { fontSize: 14, color: '#0891b2', fontWeight: '500', marginLeft: 5 },
  infoRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 12 }, infoText: { fontSize: 15, color: '#333', marginLeft: 12, flex: 1 }, link: { color: '#0891b2' },
  descripcion: { fontSize: 15, color: '#666', lineHeight: 22 },
  cursoItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#f1f5f9' },
  cursoSiglas: { fontSize: 14, fontWeight: '600', color: '#0891b2', width: 50 }, cursoNombre: { flex: 1, fontSize: 14, color: '#333' },
  comentariosTabs: { flexDirection: 'row', backgroundColor: '#f1f5f9', borderRadius: 10, padding: 4, marginBottom: 10 },
  comentariosTab: { flex: 1, alignItems: 'center', paddingVertical: 10, borderRadius: 8 }, comentariosTabActive: { backgroundColor: '#0891b2' },
  comentariosTabText: { fontSize: 13, fontWeight: '600', color: '#666' }, comentariosTabTextActive: { color: '#fff' },
  comentarioCard: { backgroundColor: '#f8fafc', padding: 12, borderRadius: 10, marginBottom: 10 },
  comentarioHeaderRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 8 },
  comentarioAutor: { fontWeight: '600', color: '#333', fontSize: 14 }, comentarioFecha: { fontSize: 12, color: '#999' },
  comentarioTexto: { fontSize: 14, color: '#555', lineHeight: 20 },
  comentariosEmpty: { alignItems: 'center', paddingVertical: 20 }, comentariosEmptyText: { marginTop: 10, fontSize: 14, color: '#999' },
  // Input de comentario
  comentarioInputContainer: { marginTop: 10, borderTopWidth: 1, borderTopColor: '#e2e8f0', paddingTop: 12 },
  comentarioInput: { backgroundColor: '#f8fafc', borderRadius: 10, padding: 12, fontSize: 14, color: '#333', borderWidth: 1, borderColor: '#e2e8f0', minHeight: 60, textAlignVertical: 'top' },
  comentarioActions: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 8 },
  privadoToggle: { flexDirection: 'row', alignItems: 'center', paddingHorizontal: 12, paddingVertical: 6, borderRadius: 20, backgroundColor: '#f1f5f9', gap: 5 },
  privadoToggleActive: { backgroundColor: '#0891b2' },
  privadoText: { fontSize: 12, fontWeight: '500', color: '#666' }, privadoTextActive: { color: '#fff' },
  enviarBtn: { width: 40, height: 40, borderRadius: 20, backgroundColor: '#0891b2', justifyContent: 'center', alignItems: 'center' },
  enviarBtnDisabled: { backgroundColor: '#ccc' },
  accionesGrid: { flexDirection: 'row', gap: 12 },
  accionCard: { flex: 1, backgroundColor: '#f8fafc', borderRadius: 12, padding: 16, alignItems: 'center', borderWidth: 1, borderColor: '#e2e8f0' },
  accionCardActive: { backgroundColor: '#fef2f2', borderColor: '#f43f5e' }, accionText: { fontSize: 14, fontWeight: '600', color: '#333', marginTop: 8 },
  profesorActions: { paddingHorizontal: 15, paddingTop: 15, gap: 10 },
  actionButtonSecondary: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#fff', paddingVertical: 14, borderRadius: 10, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  actionTextWhite: { marginLeft: 8, fontSize: 15, color: '#0891b2', fontWeight: '500' },
  actionButtonPrimary: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#fff', paddingVertical: 14, borderRadius: 10, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  actionTextGreen: { marginLeft: 8, fontSize: 15, color: '#059669', fontWeight: '600' },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', alignItems: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 16, padding: 20, width: '100%', maxWidth: 400 },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 },
  modalTitle: { fontSize: 18, fontWeight: '600', color: '#333' }, modalLabel: { fontSize: 14, fontWeight: '500', color: '#666', marginBottom: 10, marginTop: 10 },
  modalInput: { backgroundColor: '#f5f5f5', borderRadius: 10, padding: 14, fontSize: 15, borderWidth: 1, borderColor: '#e0e0e0', color: '#333' },
  optionItem: { padding: 14, borderRadius: 10, backgroundColor: '#f5f5f5', borderWidth: 1, borderColor: '#e0e0e0' },
  optionItemSelected: { backgroundColor: '#059669', borderColor: '#059669' }, optionText: { fontSize: 14, color: '#333' }, optionTextSelected: { color: '#fff', fontWeight: '500' },
  cantidadContainer: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#f5f5f5', borderRadius: 10, borderWidth: 1, borderColor: '#e0e0e0', alignSelf: 'flex-start' },
  cantidadBtn: { padding: 12 }, cantidadText: { fontSize: 18, fontWeight: '600', minWidth: 50, textAlign: 'center', color: '#333' },
  modalButtons: { flexDirection: 'row', marginTop: 20, gap: 10 },
  modalButtonCancel: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#f5f5f5', alignItems: 'center' }, modalButtonCancelText: { fontSize: 15, fontWeight: '600', color: '#666' },
  modalButtonConfirm: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#059669', alignItems: 'center' }, modalButtonDisabled: { backgroundColor: '#ccc' }, modalButtonConfirmText: { fontSize: 15, fontWeight: '600', color: '#fff' },
  estrellasContainer: { flexDirection: 'row', justifyContent: 'center', gap: 8, marginVertical: 20 },
  contactadoItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#f1f5f9' },
  contactadoProfesor: { fontSize: 14, fontWeight: '600', color: '#333' },
  contactadoDepto: { fontSize: 12, color: '#666', marginTop: 2 },
  reservaItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#f1f5f9' },
  reservaProfesor: { fontSize: 14, fontWeight: '600', color: '#333' },
  reservaDetalle: { fontSize: 12, color: '#666', marginTop: 2 },
});
