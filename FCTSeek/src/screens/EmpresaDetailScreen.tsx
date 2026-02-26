import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Linking,
  Modal,
  TextInput,
  Alert,
} from 'react-native';
import { RouteProp, useRoute, useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import {
  getEmpresaById,
  getComentariosEmpresa,
  currentUser,
  getPlazasDisponibles,
  getReservasEmpresa,
  getCursosByDepartamento,
  departamentos,
  cursos
} from '../data/mockData';
import { RootStackParamList } from '../navigation/AppNavigator';

type EmpresaDetailRouteProp = RouteProp<RootStackParamList, 'EmpresaDetail'>;
type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function EmpresaDetailScreen() {
  const route = useRoute<EmpresaDetailRouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { empresaId } = route.params;

  const [modalReservaVisible, setModalReservaVisible] = useState(false);
  const [cantidadReserva, setCantidadReserva] = useState('1');
  const [claseReserva, setClaseReserva] = useState('');
  const [cursoSeleccionado, setCursoSeleccionado] = useState<number | null>(null);
  const [deptoActual, setDeptoActual] = useState<number>(0);

  const [modalContactadoVisible, setModalContactadoVisible] = useState(false);
  const [deptoContactado, setDeptoContactado] = useState<number | null>(null);

  // Tab de comentarios
  const [tabComentarios, setTabComentarios] = useState<'general' | 'profesores'>('general');

  // Valoración y favoritos
  const [modalValorarVisible, setModalValorarVisible] = useState(false);
  const [valoracionSeleccionada, setValoracionSeleccionada] = useState(0);
  const [esFavorito, setEsFavorito] = useState(false);

  const empresa = getEmpresaById(empresaId);
  const comentarios = getComentariosEmpresa(empresaId, currentUser.rol === 'PROFESOR');

  // Filtrar comentarios según el tab
  const comentariosGenerales = comentarios.filter(c => !c.esPrivado);
  const comentariosProfesores = comentarios.filter(c => c.esPrivado);
  const comentariosMostrados = tabComentarios === 'general' ? comentariosGenerales : comentariosProfesores;

  if (!empresa) {
    return (
      <GradientBackground>
        <View style={styles.container}>
          <Text style={styles.errorText}>Empresa no encontrada</Text>
        </View>
      </GradientBackground>
    );
  }

  const plazasPorDepartamento = empresa.contactadaPor?.map(contacto => ({
    ...contacto,
    ...getPlazasDisponibles(empresaId, contacto.departamentoId),
    reservas: getReservasEmpresa(empresaId, contacto.departamentoId),
  })) || [];

  const handleCall = () => {
    if (empresa.telefono) Linking.openURL(`tel:${empresa.telefono}`);
  };

  const handleEmail = () => {
    if (empresa.email) Linking.openURL(`mailto:${empresa.email}`);
  };

  const handleEdit = () => {
    navigation.navigate('EditEmpresa', { empresaId: empresa.id });
  };

  const abrirModalReserva = (departamentoId: number) => {
    setDeptoActual(departamentoId);
    setCursoSeleccionado(null);
    setCantidadReserva('1');
    setClaseReserva('');
    setModalReservaVisible(true);
  };

  const handleReservarPlazas = () => {
    if (!cursoSeleccionado) {
      Alert.alert('Error', 'Debes seleccionar un ciclo formativo');
      return;
    }

    const cantidad = parseInt(cantidadReserva) || 1;
    const cursoInfo = cursos.find(c => c.id === cursoSeleccionado);

    console.log('Reservando plazas:', {
      empresaId,
      departamentoId: deptoActual,
      cantidad,
      cursoId: cursoSeleccionado,
      cursoSiglas: cursoInfo?.siglas,
      clase: claseReserva,
    });

    setModalReservaVisible(false);
    Alert.alert('Plazas reservadas', `Has reservado ${cantidad} plaza${cantidad > 1 ? 's' : ''} para ${cursoInfo?.siglas}${claseReserva ? ` - Clase ${claseReserva}` : ''}`);
  };

  const abrirModalContactado = () => {
    setDeptoContactado(null);
    setModalContactadoVisible(true);
  };

  const handleMarcarContactado = () => {
    if (!deptoContactado) {
      Alert.alert('Error', 'Debes seleccionar un departamento');
      return;
    }

    const deptoInfo = departamentos.find(d => d.id === deptoContactado);

    console.log('Marcando como contactado:', {
      empresaId,
      departamentoId: deptoContactado,
      departamentoNombre: deptoInfo?.nombre,
      profesorId: currentUser.id,
      profesorNombre: `${currentUser.nombre} ${currentUser.apellidos}`,
      fecha: new Date().toISOString().split('T')[0],
    });

    setModalContactadoVisible(false);
    Alert.alert('Empresa contactada', `Has marcado la empresa como contactada por ${deptoInfo?.nombre}`);
  };

  const handleDesmarcarContactado = (departamentoId: number, departamentoNombre: string) => {
    Alert.alert(
      'Desmarcar contacto',
      `¿Estás seguro de que quieres eliminar el contacto de "${departamentoNombre}"?\n\nEsto también eliminará las plazas y reservas asociadas.`,
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Eliminar',
          style: 'destructive',
          onPress: () => {
            console.log('Desmarcando contacto:', { empresaId, departamentoId });
            Alert.alert('Contacto eliminado', `Se ha eliminado el contacto de ${departamentoNombre}`);
          }
        },
      ]
    );
  };

  const handleValorar = () => {
    if (valoracionSeleccionada === 0) {
      Alert.alert('Error', 'Debes seleccionar una valoración');
      return;
    }

    console.log('Valorando empresa:', {
      empresaId,
      valoracion: valoracionSeleccionada,
      usuarioId: currentUser.id,
    });

    setModalValorarVisible(false);
    Alert.alert('¡Gracias!', `Has valorado esta empresa con ${valoracionSeleccionada} estrella${valoracionSeleccionada > 1 ? 's' : ''}`);
    setValoracionSeleccionada(0);
  };

  const toggleFavorito = () => {
    setEsFavorito(!esFavorito);
    Alert.alert(
      esFavorito ? 'Eliminado de favoritos' : 'Añadido a favoritos',
      esFavorito ? 'La empresa se ha eliminado de tu lista de favoritos' : 'La empresa se ha añadido a tu lista de favoritos'
    );
  };

  const cursosDelDepto = getCursosByDepartamento(deptoActual);

  return (
    <GradientBackground>
      <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
        {/* Cabecera */}
        <View style={styles.header}>
          <Text style={styles.nombre}>{empresa.nombre}</Text>

          {empresa.valoracionMedia && (
            <View style={styles.valoracionContainer}>
              <View style={styles.estrellas}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <Ionicons
                    key={star}
                    name={star <= empresa.valoracionMedia! ? 'star' : 'star-outline'}
                    size={20}
                    color="#fbbf24"
                  />
                ))}
              </View>
              <Text style={styles.valoracionTexto}>
                {empresa.valoracionMedia.toFixed(1)} ({empresa.totalValoraciones} valoraciones)
              </Text>
            </View>
          )}

          <View style={styles.tags}>
            {empresa.departamentos.map((dept) => (
              <View key={dept.id} style={styles.tag}>
                <Text style={styles.tagText}>{dept.nombre}</Text>
              </View>
            ))}
          </View>
        </View>

        {/* Plazas disponibles */}
        {plazasPorDepartamento.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>📊 Plazas disponibles</Text>

            {plazasPorDepartamento.map((info, index) => (
              <View key={index} style={styles.plazasCard}>
                <View style={styles.plazasHeader}>
                  <View>
                    <Text style={styles.plazasDepto}>{info.departamentoNombre}</Text>
                    {info.esGeneral && (
                      <View style={styles.generalBadge}>
                        <Ionicons name="people" size={12} color="#0891b2" />
                        <Text style={styles.generalText}>Cualquier ciclo</Text>
                      </View>
                    )}
                  </View>
                  <View style={[styles.plazasBadge, info.libres > 0 ? styles.plazasBadgeDisponible : styles.plazasBadgeLleno]}>
                    <Text style={[styles.plazasBadgeText, info.libres > 0 ? styles.plazasBadgeTextVerde : styles.plazasBadgeTextRojo]}>
                      {info.libres > 0 ? `${info.libres} libres` : 'Completo'}
                    </Text>
                  </View>
                </View>

                <View style={styles.plazasInfo}>
                  <View style={styles.plazasItem}>
                    <Text style={styles.plazasNumero}>{info.ofertadas}</Text>
                    <Text style={styles.plazasLabel}>Ofertadas</Text>
                  </View>
                  <View style={styles.plazasDivider} />
                  <View style={styles.plazasItem}>
                    <Text style={styles.plazasNumero}>{info.reservadas}</Text>
                    <Text style={styles.plazasLabel}>Reservadas</Text>
                  </View>
                  <View style={styles.plazasDivider} />
                  <View style={styles.plazasItem}>
                    <Text style={[styles.plazasNumero, { color: info.libres > 0 ? '#10b981' : '#ef4444' }]}>
                      {info.libres}
                    </Text>
                    <Text style={styles.plazasLabel}>Libres</Text>
                  </View>
                </View>

                {info.reservas && info.reservas.length > 0 && (
                  <View style={styles.reservasDetalle}>
                    <Text style={styles.reservasTitle}>Reservas realizadas:</Text>
                    {info.reservas.map((reserva, rIdx) => (
                      <View key={rIdx} style={styles.reservaItemRow}>
                        <View style={styles.reservaIcon}>
                          <Ionicons name="person-circle-outline" size={20} color="#0891b2" />
                        </View>
                        <View style={styles.reservaInfo}>
                          <Text style={styles.reservaProfesor}>{reserva.profesorNombre}</Text>
                          <Text style={styles.reservaDetalle}>
                            {reserva.cantidad} plaza{reserva.cantidad > 1 ? 's' : ''}
                            {reserva.cursoSiglas ? ` • ${reserva.cursoSiglas}` : ''}
                            {reserva.clase && ` (${reserva.clase})`}
                          </Text>
                        </View>
                      </View>
                    ))}
                  </View>
                )}

                {currentUser.rol === 'PROFESOR' && info.libres > 0 && (
                  <TouchableOpacity style={styles.reservarBtn} onPress={() => abrirModalReserva(info.departamentoId)}>
                    <Ionicons name="add-circle-outline" size={18} color="#0891b2" />
                    <Text style={styles.reservarBtnText}>Reservar plazas para mis alumnos</Text>
                  </TouchableOpacity>
                )}
              </View>
            ))}
          </View>
        )}

        {/* Contactado por */}
        {empresa.contactadaPor && empresa.contactadaPor.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>📞 Contactado por</Text>
            {empresa.contactadaPor.map((contacto, index) => (
              <View key={index} style={styles.contactadoCard}>
                <View style={styles.contactadoRow}>
                  <View style={styles.contactadoInfo}>
                    <View style={styles.contactadoHeader}>
                      <Ionicons name="checkmark-circle" size={18} color="#10b981" />
                      <Text style={styles.contactadoDepto}>{contacto.departamentoNombre}</Text>
                    </View>
                    <Text style={styles.contactadoProfesor}>
                      {contacto.profesorNombre} • {contacto.fecha}
                    </Text>
                  </View>
                  {currentUser.rol === 'PROFESOR' && contacto.profesorId === currentUser.id && (
                    <TouchableOpacity
                      style={styles.eliminarContactoBtn}
                      onPress={() => handleDesmarcarContactado(contacto.departamentoId, contacto.departamentoNombre)}
                    >
                      <Ionicons name="close-circle" size={24} color="#ef4444" />
                    </TouchableOpacity>
                  )}
                </View>
              </View>
            ))}
          </View>
        )}

        {/* Información de contacto */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Información de contacto</Text>

          {empresa.direccion && (
            <View style={styles.infoRow}>
              <Ionicons name="location-outline" size={20} color="#666" />
              <Text style={styles.infoText}>{empresa.direccion}, {empresa.ciudad}</Text>
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
              <Text style={styles.infoText}>
                Contacto: {empresa.personaContacto}
                {empresa.telefonoContacto && ` (${empresa.telefonoContacto})`}
              </Text>
            </View>
          )}
        </View>

        {/* Descripción */}
        {empresa.descripcion && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Descripción</Text>
            <Text style={styles.descripcion}>{empresa.descripcion}</Text>
          </View>
        )}

        {/* Cursos */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Ciclos formativos</Text>
          {empresa.cursos.map((curso) => (
            <View key={curso.id} style={styles.cursoItem}>
              <Text style={styles.cursoSiglas}>{curso.siglas}</Text>
              <Text style={styles.cursoNombre}>{curso.nombre}</Text>
            </View>
          ))}
        </View>

        {/* Comentarios con Tabs */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Comentarios</Text>

          {/* Tabs de comentarios */}
          <View style={styles.comentariosTabs}>
            <TouchableOpacity
              style={[styles.comentariosTab, tabComentarios === 'general' && styles.comentariosTabActive]}
              onPress={() => setTabComentarios('general')}
            >
              <Ionicons
                name="globe-outline"
                size={18}
                color={tabComentarios === 'general' ? '#fff' : '#666'}
              />
              <Text style={[styles.comentariosTabText, tabComentarios === 'general' && styles.comentariosTabTextActive]}>
                General ({comentariosGenerales.length})
              </Text>
            </TouchableOpacity>

            {currentUser.rol === 'PROFESOR' && (
              <TouchableOpacity
                style={[styles.comentariosTab, tabComentarios === 'profesores' && styles.comentariosTabActive]}
                onPress={() => setTabComentarios('profesores')}
              >
                <Ionicons
                  name="school-outline"
                  size={18}
                  color={tabComentarios === 'profesores' ? '#fff' : '#666'}
                />
                <Text style={[styles.comentariosTabText, tabComentarios === 'profesores' && styles.comentariosTabTextActive]}>
                  Profesores ({comentariosProfesores.length})
                </Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Descripción del tab */}
          <View style={styles.tabDescripcion}>
            <Ionicons
              name={tabComentarios === 'general' ? 'information-circle-outline' : 'lock-closed-outline'}
              size={14}
              color="#666"
            />
            <Text style={styles.tabDescripcionText}>
              {tabComentarios === 'general'
                ? 'Visible para todos los usuarios'
                : 'Solo visible para profesores'}
            </Text>
          </View>

          {/* Lista de comentarios */}
          {comentariosMostrados.length > 0 ? (
            comentariosMostrados.map((comentario) => (
              <View key={comentario.id} style={styles.comentarioCard}>
                <View style={styles.comentarioHeaderRow}>
                  <View style={styles.comentarioAutorContainer}>
                    <View style={styles.comentarioAvatar}>
                      <Text style={styles.comentarioAvatarText}>
                        {comentario.usuarioNombre.charAt(0)}
                      </Text>
                    </View>
                    <View>
                      <Text style={styles.comentarioAutor}>{comentario.usuarioNombre}</Text>
                      <Text style={styles.comentarioRol}>
                        {comentario.esPrivado ? 'Profesor' : 'Usuario'}
                      </Text>
                    </View>
                  </View>
                  <Text style={styles.comentarioFecha}>{comentario.fecha}</Text>
                </View>
                <Text style={styles.comentarioTexto}>{comentario.texto}</Text>
              </View>
            ))
          ) : (
            <View style={styles.comentariosEmpty}>
              <Ionicons name="chatbubble-outline" size={40} color="#ccc" />
              <Text style={styles.comentariosEmptyText}>
                No hay comentarios {tabComentarios === 'profesores' ? 'de profesores' : ''} aún
              </Text>
            </View>
          )}

          {/* Botón escribir comentario */}
          <TouchableOpacity style={styles.escribirComentarioBtn}>
            <Ionicons name="create-outline" size={18} color="#0891b2" />
            <Text style={styles.escribirComentarioBtnText}>
              Escribir comentario {tabComentarios === 'profesores' ? 'privado' : ''}
            </Text>
          </TouchableOpacity>
        </View>

        {/* Acciones rápidas */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Acciones</Text>

          <View style={styles.accionesGrid}>
            <TouchableOpacity
              style={[styles.accionCard, esFavorito && styles.accionCardActive]}
              onPress={toggleFavorito}
            >
              <View style={[styles.accionIconContainer, esFavorito && styles.accionIconContainerActive]}>
                <Ionicons
                  name={esFavorito ? "heart" : "heart-outline"}
                  size={24}
                  color={esFavorito ? "#fff" : "#f43f5e"}
                />
              </View>
              <Text style={[styles.accionText, esFavorito && styles.accionTextActive]}>
                {esFavorito ? 'En favoritos' : 'Favorito'}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.accionCard}
              onPress={() => {
                setValoracionSeleccionada(0);
                setModalValorarVisible(true);
              }}
            >
              <View style={styles.accionIconContainer}>
                <Ionicons name="star-outline" size={24} color="#f59e0b" />
              </View>
              <Text style={styles.accionText}>Valorar</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Acciones de profesor */}
        {currentUser.rol === 'PROFESOR' && (
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
      <Modal animationType="fade" transparent={true} visible={modalReservaVisible} onRequestClose={() => setModalReservaVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Reservar plazas</Text>
              <TouchableOpacity onPress={() => setModalReservaVisible(false)}>
                <Ionicons name="close" size={24} color="#666" />
              </TouchableOpacity>
            </View>

            <Text style={styles.modalLabel}>Ciclo formativo *</Text>
            <ScrollView style={styles.pickerScroll} nestedScrollEnabled={true}>
              <View style={styles.pickerContainer}>
                {cursosDelDepto.map((curso) => (
                  <TouchableOpacity
                    key={curso.id}
                    style={[styles.optionItem, cursoSeleccionado === curso.id && styles.optionItemSelected]}
                    onPress={() => setCursoSeleccionado(curso.id)}
                  >
                    <Text style={[styles.optionText, cursoSeleccionado === curso.id && styles.optionTextSelected]}>
                      {curso.siglas} - {curso.nombre}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </ScrollView>

            <Text style={styles.modalLabel}>Cantidad de plazas</Text>
            <View style={styles.cantidadContainer}>
              <TouchableOpacity style={styles.cantidadBtn} onPress={() => setCantidadReserva(String(Math.max(1, parseInt(cantidadReserva) - 1)))}>
                <Ionicons name="remove" size={20} color="#059669" />
              </TouchableOpacity>
              <Text style={styles.cantidadText}>{cantidadReserva}</Text>
              <TouchableOpacity style={styles.cantidadBtn} onPress={() => setCantidadReserva(String(parseInt(cantidadReserva) + 1))}>
                <Ionicons name="add" size={20} color="#059669" />
              </TouchableOpacity>
            </View>

            <Text style={styles.modalLabel}>Clase (opcional)</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ej: 2A, 1B..."
              placeholderTextColor="#999"
              value={claseReserva}
              onChangeText={setClaseReserva}
              maxLength={5}
            />

            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalReservaVisible(false)}>
                <Text style={styles.modalButtonCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalButtonConfirm, !cursoSeleccionado && styles.modalButtonDisabled]}
                onPress={handleReservarPlazas}
                disabled={!cursoSeleccionado}
              >
                <Text style={styles.modalButtonConfirmText}>Reservar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal marcar contactado */}
      <Modal animationType="fade" transparent={true} visible={modalContactadoVisible} onRequestClose={() => setModalContactadoVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Marcar como contactado</Text>
              <TouchableOpacity onPress={() => setModalContactadoVisible(false)}>
                <Ionicons name="close" size={24} color="#666" />
              </TouchableOpacity>
            </View>

            <Text style={styles.modalLabel}>¿Por qué departamento se ha contactado?</Text>
            <View style={styles.pickerContainer}>
              {departamentos.map((depto) => (
                <TouchableOpacity
                  key={depto.id}
                  style={[styles.optionItem, deptoContactado === depto.id && styles.optionItemSelected]}
                  onPress={() => setDeptoContactado(depto.id)}
                >
                  <View style={styles.optionContent}>
                    <Text style={[styles.optionCode, deptoContactado === depto.id && styles.optionCodeSelected]}>
                      {depto.codigo}
                    </Text>
                    <Text style={[styles.optionText, deptoContactado === depto.id && styles.optionTextSelected]}>
                      {depto.nombre}
                    </Text>
                  </View>
                </TouchableOpacity>
              ))}
            </View>

            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalContactadoVisible(false)}>
                <Text style={styles.modalButtonCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalButtonConfirm, !deptoContactado && styles.modalButtonDisabled]}
                onPress={handleMarcarContactado}
                disabled={!deptoContactado}
              >
                <Text style={styles.modalButtonConfirmText}>Confirmar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Modal valorar */}
      <Modal animationType="fade" transparent={true} visible={modalValorarVisible} onRequestClose={() => setModalValorarVisible(false)}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Valorar empresa</Text>
              <TouchableOpacity onPress={() => setModalValorarVisible(false)}>
                <Ionicons name="close" size={24} color="#666" />
              </TouchableOpacity>
            </View>

            <Text style={styles.valorarSubtitle}>¿Cómo valorarías tu experiencia con esta empresa?</Text>

            {/* Estrellas */}
            <View style={styles.estrellasContainer}>
              {[1, 2, 3, 4, 5].map((star) => (
                <TouchableOpacity
                  key={star}
                  onPress={() => setValoracionSeleccionada(star)}
                  style={styles.estrellaBtn}
                >
                  <Ionicons
                    name={star <= valoracionSeleccionada ? 'star' : 'star-outline'}
                    size={40}
                    color={star <= valoracionSeleccionada ? '#f59e0b' : '#d1d5db'}
                  />
                </TouchableOpacity>
              ))}
            </View>

            {/* Texto de la valoración */}
            <Text style={styles.valoracionTextoModal}>
              {valoracionSeleccionada === 0 && 'Selecciona una valoración'}
              {valoracionSeleccionada === 1 && '⭐ Muy mala'}
              {valoracionSeleccionada === 2 && '⭐⭐ Mala'}
              {valoracionSeleccionada === 3 && '⭐⭐⭐ Regular'}
              {valoracionSeleccionada === 4 && '⭐⭐⭐⭐ Buena'}
              {valoracionSeleccionada === 5 && '⭐⭐⭐⭐⭐ Excelente'}
            </Text>

            <View style={styles.modalButtons}>
              <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalValorarVisible(false)}>
                <Text style={styles.modalButtonCancelText}>Cancelar</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalButtonConfirm, styles.modalButtonValorar, valoracionSeleccionada === 0 && styles.modalButtonDisabled]}
                onPress={handleValorar}
                disabled={valoracionSeleccionada === 0}
              >
                <Ionicons name="star" size={18} color="#fff" />
                <Text style={styles.modalButtonConfirmText}>Valorar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  errorText: { color: '#fff', fontSize: 16, textAlign: 'center', marginTop: 50 },

  // Cabecera
  header: { padding: 20 },
  nombre: { fontSize: 24, fontWeight: 'bold', color: '#fff', marginBottom: 10 },
  valoracionContainer: { flexDirection: 'row', alignItems: 'center', marginBottom: 15 },
  estrellas: { flexDirection: 'row' },
  valoracionTexto: { marginLeft: 10, fontSize: 14, color: 'rgba(255,255,255,0.8)' },
  tags: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  tag: { backgroundColor: 'rgba(255,255,255,0.2)', paddingHorizontal: 12, paddingVertical: 5, borderRadius: 8 },
  tagText: { color: '#fff', fontSize: 12, fontWeight: '500' },

  // Secciones
  section: { backgroundColor: '#fff', marginHorizontal: 15, marginBottom: 15, borderRadius: 12, padding: 15, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#333', marginBottom: 15 },

  // Plazas
  plazasCard: { backgroundColor: '#f8fafc', borderRadius: 10, padding: 15, marginBottom: 10, borderWidth: 1, borderColor: '#e2e8f0' },
  plazasHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 },
  plazasDepto: { fontSize: 15, fontWeight: '600', color: '#333' },
  generalBadge: { flexDirection: 'row', alignItems: 'center', marginTop: 4 },
  generalText: { fontSize: 12, color: '#0891b2', marginLeft: 4, fontWeight: '500' },
  plazasBadge: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
  plazasBadgeDisponible: { backgroundColor: '#d1fae5' },
  plazasBadgeLleno: { backgroundColor: '#fee2e2' },
  plazasBadgeText: { fontSize: 12, fontWeight: '600' },
  plazasBadgeTextVerde: { color: '#065f46' },
  plazasBadgeTextRojo: { color: '#991b1b' },
  plazasInfo: { flexDirection: 'row', justifyContent: 'space-around', alignItems: 'center' },
  plazasItem: { alignItems: 'center' },
  plazasNumero: { fontSize: 24, fontWeight: 'bold', color: '#333' },
  plazasLabel: { fontSize: 12, color: '#666', marginTop: 2 },
  plazasDivider: { width: 1, height: 40, backgroundColor: '#e2e8f0' },
  reservasDetalle: { marginTop: 12, paddingTop: 12, borderTopWidth: 1, borderTopColor: '#e2e8f0' },
  reservasTitle: { fontSize: 13, fontWeight: '600', color: '#666', marginBottom: 8 },
  reservaItemRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  reservaIcon: { marginRight: 10 },
  reservaInfo: { flex: 1 },
  reservaProfesor: { fontSize: 14, fontWeight: '500', color: '#333' },
  reservaDetalle: { fontSize: 12, color: '#666' },
  reservarBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', marginTop: 12, paddingTop: 12, borderTopWidth: 1, borderTopColor: '#e2e8f0' },
  reservarBtnText: { fontSize: 14, color: '#0891b2', fontWeight: '500', marginLeft: 5 },

  // Contactado
  contactadoCard: { backgroundColor: '#f0fdf4', padding: 12, borderRadius: 8, borderLeftWidth: 3, borderLeftColor: '#10b981', marginBottom: 8 },
  contactadoRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  contactadoInfo: { flex: 1 },
  contactadoHeader: { flexDirection: 'row', alignItems: 'center' },
  contactadoDepto: { fontWeight: '600', color: '#059669', marginLeft: 6 },
  contactadoProfesor: { fontSize: 13, color: '#666', marginTop: 4 },
  eliminarContactoBtn: { padding: 5 },

  // Info rows
  infoRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 12 },
  infoText: { fontSize: 15, color: '#333', marginLeft: 12, flex: 1 },
  link: { color: '#0891b2' },
  descripcion: { fontSize: 15, color: '#666', lineHeight: 22 },

  // Cursos
  cursoItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#f1f5f9' },
  cursoSiglas: { fontSize: 14, fontWeight: '600', color: '#0891b2', width: 50 },
  cursoNombre: { flex: 1, fontSize: 14, color: '#333' },

  // Tabs de comentarios
  comentariosTabs: {
    flexDirection: 'row',
    backgroundColor: '#f1f5f9',
    borderRadius: 10,
    padding: 4,
    marginBottom: 10,
  },
  comentariosTab: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 10,
    borderRadius: 8,
    gap: 6,
  },
  comentariosTabActive: {
    backgroundColor: '#0891b2',
  },
  comentariosTabText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#666',
  },
  comentariosTabTextActive: {
    color: '#fff',
  },
  tabDescripcion: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#f8fafc',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 6,
    marginBottom: 15,
    gap: 6,
  },
  tabDescripcionText: {
    fontSize: 12,
    color: '#666',
  },

  // Comentarios
  comentarioCard: {
    backgroundColor: '#f8fafc',
    padding: 12,
    borderRadius: 10,
    marginBottom: 10,
  },
  comentarioHeaderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 10,
  },
  comentarioAutorContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  comentarioAvatar: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: '#0891b2',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 10,
  },
  comentarioAvatarText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  comentarioAutor: {
    fontWeight: '600',
    color: '#333',
    fontSize: 14,
  },
  comentarioRol: {
    fontSize: 11,
    color: '#888',
  },
  comentarioFecha: {
    fontSize: 12,
    color: '#999',
  },
  comentarioTexto: {
    fontSize: 14,
    color: '#555',
    lineHeight: 20,
  },

  // Empty state
  comentariosEmpty: {
    alignItems: 'center',
    paddingVertical: 30,
  },
  comentariosEmptyText: {
    marginTop: 10,
    fontSize: 14,
    color: '#999',
  },

  // Botón escribir
  escribirComentarioBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f0fdfa',
    paddingVertical: 12,
    borderRadius: 8,
    marginTop: 10,
    borderWidth: 1,
    borderColor: '#0891b2',
    borderStyle: 'dashed',
    gap: 6,
  },
  escribirComentarioBtnText: {
    fontSize: 14,
    color: '#0891b2',
    fontWeight: '500',
  },

  // Acciones grid
  accionesGrid: {
    flexDirection: 'row',
    gap: 12,
  },
  accionCard: {
    flex: 1,
    backgroundColor: '#f8fafc',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#e2e8f0',
  },
  accionCardActive: {
    backgroundColor: '#fef2f2',
    borderColor: '#f43f5e',
  },
  accionIconContainer: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  accionIconContainerActive: {
    backgroundColor: '#f43f5e',
  },
  accionText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  accionTextActive: {
    color: '#f43f5e',
  },

  // Acciones profesor
  profesorActions: { paddingHorizontal: 15, paddingTop: 15, gap: 10 },
  actionButtonSecondary: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#fff', paddingVertical: 14, borderRadius: 10, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  actionTextWhite: { marginLeft: 8, fontSize: 15, color: '#0891b2', fontWeight: '500' },
  actionButtonPrimary: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#fff', paddingVertical: 14, borderRadius: 10, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  actionTextGreen: { marginLeft: 8, fontSize: 15, color: '#059669', fontWeight: '600' },

  // Modal
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', alignItems: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 16, padding: 20, width: '100%', maxWidth: 400 },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 },
  modalTitle: { fontSize: 18, fontWeight: '600', color: '#333' },
  modalLabel: { fontSize: 14, fontWeight: '500', color: '#666', marginBottom: 10, marginTop: 10 },
  modalInput: { backgroundColor: '#f5f5f5', borderRadius: 10, padding: 14, fontSize: 15, borderWidth: 1, borderColor: '#e0e0e0', color: '#333' },

  pickerScroll: { maxHeight: 150 },
  pickerContainer: { gap: 8 },
  optionItem: { padding: 14, borderRadius: 10, backgroundColor: '#f5f5f5', borderWidth: 1, borderColor: '#e0e0e0' },
  optionItemSelected: { backgroundColor: '#059669', borderColor: '#059669' },
  optionContent: { flexDirection: 'row', alignItems: 'center' },
  optionCode: { fontSize: 14, fontWeight: '700', color: '#059669', marginRight: 10 },
  optionCodeSelected: { color: '#fff' },
  optionText: { fontSize: 14, color: '#333', flex: 1 },
  optionTextSelected: { color: '#fff', fontWeight: '500' },

  cantidadContainer: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#f5f5f5', borderRadius: 10, borderWidth: 1, borderColor: '#e0e0e0', alignSelf: 'flex-start' },
  cantidadBtn: { padding: 12 },
  cantidadText: { fontSize: 18, fontWeight: '600', minWidth: 50, textAlign: 'center', color: '#333' },
  modalButtons: { flexDirection: 'row', marginTop: 20, gap: 10 },
  modalButtonCancel: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#f5f5f5', alignItems: 'center' },
  modalButtonCancelText: { fontSize: 15, fontWeight: '600', color: '#666' },
  modalButtonConfirm: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#059669', alignItems: 'center' },
  modalButtonDisabled: { backgroundColor: '#ccc' },
  modalButtonConfirmText: { fontSize: 15, fontWeight: '600', color: '#fff' },

  // Modal valorar
  valorarSubtitle: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 20,
  },
  estrellasContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 8,
    marginBottom: 15,
  },
  estrellaBtn: {
    padding: 5,
  },
  valoracionTextoModal: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    textAlign: 'center',
    marginBottom: 20,
  },
  modalButtonValorar: {
    backgroundColor: '#f59e0b',
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 6,
  },
});