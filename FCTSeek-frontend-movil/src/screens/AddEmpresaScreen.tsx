// src/screens/AddEmpresaScreen.tsx
import React, { useState, useEffect } from 'react';
import {
  View, Text, TextInput, StyleSheet, ScrollView,
  TouchableOpacity, Alert, ActivityIndicator,
} from 'react-native';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import { empresaService } from '../services/empresaService';
import { plazaService } from '../services/plazaService';
import { departamentoService } from '../services/departamentoService';
import { Departamento, Curso } from '../types';
import { RootStackParamList } from '../navigation/AppNavigator';

type AddEmpresaRouteProp = RouteProp<RootStackParamList, 'EditEmpresa'>;

interface PlazaDepartamento {
  departamentoId: number;
  departamentoCodigo: string;
  esGeneral: boolean;
  plazasGenerales: number;
  plazasPorCurso: { cursoId: number; cursoSiglas: string; cantidad: number }[];
}

export default function AddEmpresaScreen() {
  const navigation = useNavigation();
  const route = useRoute<AddEmpresaRouteProp>();
  const empresaId = route.params?.empresaId;
  const isEditing = !!empresaId;

  const [nombre, setNombre] = useState('');
  const [telefono, setTelefono] = useState('');
  const [email, setEmail] = useState('');
  const [direccion, setDireccion] = useState('');
  const [ciudad, setCiudad] = useState('');
  const [codigoPostal, setCodigoPostal] = useState('');
  const [personaContacto, setPersonaContacto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [plazasDepartamentos, setPlazasDepartamentos] = useState<PlazaDepartamento[]>([]);
  const [deptosAceptados, setDeptosAceptados] = useState<number[]>([]);

  const [departamentos, setDepartamentos] = useState<Departamento[]>([]);
  const [cursosPorDepto, setCursosPorDepto] = useState<Record<number, Curso[]>>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const deps = await departamentoService.getAll();
      setDepartamentos(deps);

      // Cargar cursos de cada departamento
      const cursosMap: Record<number, Curso[]> = {};
      for (const dep of deps) {
        const cursos = await departamentoService.getCursos(dep.id);
        cursosMap[dep.id] = cursos;
      }
      setCursosPorDepto(cursosMap);

      // Si estamos editando, cargar la empresa
      if (isEditing && empresaId) {
        const empresa = await empresaService.getById(empresaId);
        setNombre(empresa.nombre);
        setTelefono(empresa.telefono || '');
        setEmail(empresa.email || '');
        setDireccion(empresa.direccion || '');
        setCiudad(empresa.ciudad);
        setCodigoPostal(empresa.codigoPostal || '');
        setPersonaContacto(empresa.personaContacto || '');
        setDescripcion(empresa.descripcion || '');
        if (empresa.departamentos) {
          setDeptosAceptados(empresa.departamentos.map(d => d.id));
        }
      }
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      setLoading(false);
    }
  };

  const toggleDepto = (deptoId: number, deptoCodigo: string) => {
    setPlazasDepartamentos(prev => {
      const exists = prev.find(p => p.departamentoId === deptoId);
      if (exists) return prev.filter(p => p.departamentoId !== deptoId);
      return [...prev, { departamentoId: deptoId, departamentoCodigo: deptoCodigo, esGeneral: true, plazasGenerales: 1, plazasPorCurso: [] }];
    });
  };

  const toggleGeneralDepto = (deptoId: number, esGeneral: boolean) => {
    setPlazasDepartamentos(prev => prev.map(p => p.departamentoId === deptoId ? { ...p, esGeneral } : p));
  };

  const updatePlazasGenerales = (deptoId: number, cantidad: number) => {
    setPlazasDepartamentos(prev => prev.map(p => p.departamentoId === deptoId ? { ...p, plazasGenerales: Math.max(1, cantidad) } : p));
  };

  const toggleCursoEnDepto = (deptoId: number, cursoId: number, cursoSiglas: string) => {
    setPlazasDepartamentos(prev => prev.map(p => {
      if (p.departamentoId !== deptoId) return p;
      const exists = p.plazasPorCurso.find(c => c.cursoId === cursoId);
      if (exists) return { ...p, plazasPorCurso: p.plazasPorCurso.filter(c => c.cursoId !== cursoId) };
      return { ...p, plazasPorCurso: [...p.plazasPorCurso, { cursoId, cursoSiglas, cantidad: 1 }] };
    }));
  };

  const updatePlazasCurso = (deptoId: number, cursoId: number, cantidad: number) => {
    setPlazasDepartamentos(prev => prev.map(p => {
      if (p.departamentoId !== deptoId) return p;
      return { ...p, plazasPorCurso: p.plazasPorCurso.map(c => c.cursoId === cursoId ? { ...c, cantidad: Math.max(1, cantidad) } : c) };
    }));
  };

  const getTotalPlazas = () => {
    return plazasDepartamentos.reduce((total, p) => {
      if (p.esGeneral) return total + p.plazasGenerales;
      return total + p.plazasPorCurso.reduce((sum, c) => sum + c.cantidad, 0);
    }, 0);
  };

  const handleSubmit = async () => {
    if (!nombre.trim()) { Alert.alert('Error', 'El nombre es obligatorio'); return; }
    if (!ciudad.trim()) { Alert.alert('Error', 'La ciudad es obligatoria'); return; }

    setSaving(true);
    try {
      const empresaData = {
        nombre, telefono, email, direccion, ciudad, codigoPostal, personaContacto, descripcion,
        activa: true,
        departamentosIds: deptosAceptados,
      };

      let empresaResultId: number | undefined;

      if (isEditing && empresaId) {
        await empresaService.actualizar(empresaId, empresaData);
        empresaResultId = empresaId;
      } else {
        const nuevaEmpresa = await empresaService.crear(empresaData);
        empresaResultId = nuevaEmpresa.id;
      }

      // Crear plazas para cada departamento configurado
      if (empresaResultId && plazasDepartamentos.length > 0) {
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth();
        const cursoAcademico = month >= 8 ? `${year}/${year + 1}` : `${year - 1}/${year}`;

        for (const pd of plazasDepartamentos) {
          if (pd.esGeneral) {
            // Plaza general (sin cursoId)
            await plazaService.crear({
              empresaId: empresaResultId,
              departamentoId: pd.departamentoId,
              cantidad: pd.plazasGenerales,
              cursoAcademico,
            });
          } else {
            // Una plaza por cada curso seleccionado
            for (const cp of pd.plazasPorCurso) {
              await plazaService.crear({
                empresaId: empresaResultId,
                departamentoId: pd.departamentoId,
                cursoId: cp.cursoId,
                cantidad: cp.cantidad,
                cursoAcademico,
              });
            }
          }
        }
      }

      Alert.alert('Éxito', isEditing ? 'Empresa actualizada correctamente' : 'Empresa añadida correctamente');
      navigation.goBack();
    } catch (error) {
      Alert.alert('Error', 'No se pudo guardar la empresa');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <GradientBackground>
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </GradientBackground>
    );
  }

  return (
    <GradientBackground>
      <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
        <View style={styles.form}>
          {/* Información básica */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Información básica</Text>
            <Text style={styles.label}>Nombre *</Text>
            <TextInput style={styles.input} value={nombre} onChangeText={setNombre} placeholder="Nombre de la empresa" placeholderTextColor="#999" />
            <Text style={styles.label}>Teléfono</Text>
            <TextInput style={styles.input} value={telefono} onChangeText={setTelefono} placeholder="928 000 000" placeholderTextColor="#999" keyboardType="phone-pad" />
            <Text style={styles.label}>Email</Text>
            <TextInput style={styles.input} value={email} onChangeText={setEmail} placeholder="contacto@empresa.com" placeholderTextColor="#999" keyboardType="email-address" autoCapitalize="none" />
            <Text style={styles.label}>Persona de contacto</Text>
            <TextInput style={styles.input} value={personaContacto} onChangeText={setPersonaContacto} placeholder="Nombre del contacto" placeholderTextColor="#999" />
          </View>

          {/* Ubicación */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Ubicación</Text>
            <Text style={styles.label}>Dirección</Text>
            <TextInput style={styles.input} value={direccion} onChangeText={setDireccion} placeholder="Calle, número..." placeholderTextColor="#999" />
            <Text style={styles.label}>Ciudad *</Text>
            <TextInput style={styles.input} value={ciudad} onChangeText={setCiudad} placeholder="Ciudad" placeholderTextColor="#999" />
            <Text style={styles.label}>Código postal</Text>
            <TextInput style={styles.input} value={codigoPostal} onChangeText={setCodigoPostal} placeholder="35000" placeholderTextColor="#999" keyboardType="number-pad" maxLength={5} />
          </View>

          {/* Departamentos que acepta */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Departamentos que acepta</Text>
            <Text style={styles.sectionSubtitle}>Selecciona los departamentos interesados en esta empresa</Text>
            {departamentos.map((depto) => {
              const selected = deptosAceptados.includes(depto.id);
              return (
                <TouchableOpacity
                  key={depto.id}
                  style={[styles.deptoCard, selected && styles.deptoCardSelected]}
                  onPress={() => setDeptosAceptados(prev =>
                    selected ? prev.filter(id => id !== depto.id) : [...prev, depto.id]
                  )}
                >
                  <View style={[styles.checkbox, selected && styles.checkboxSelected]}>
                    {selected && <Ionicons name="checkmark" size={16} color="#fff" />}
                  </View>
                  <View style={styles.deptoInfo}>
                    <Text style={[styles.deptoCodigo, selected && styles.deptoCodigoSelected]}>{depto.codigo}</Text>
                    <Text style={styles.deptoNombre}>{depto.nombre}</Text>
                  </View>
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Plazas a ofrecer */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Plazas a ofrecer</Text>
            <Text style={styles.sectionSubtitle}>Selecciona los departamentos y configura las plazas</Text>

            {departamentos.map((depto) => {
              const plazaDepto = plazasDepartamentos.find(p => p.departamentoId === depto.id);
              const isSelected = !!plazaDepto;
              const cursosDepto = cursosPorDepto[depto.id] || [];

              return (
                <View key={depto.id} style={styles.deptoContainer}>
                  <TouchableOpacity style={[styles.deptoCard, isSelected && styles.deptoCardSelected]} onPress={() => toggleDepto(depto.id, depto.codigo)}>
                    <View style={[styles.checkbox, isSelected && styles.checkboxSelected]}>
                      {isSelected && <Ionicons name="checkmark" size={16} color="#fff" />}
                    </View>
                    <View style={styles.deptoInfo}>
                      <Text style={[styles.deptoCodigo, isSelected && styles.deptoCodigoSelected]}>{depto.codigo}</Text>
                      <Text style={styles.deptoNombre}>{depto.nombre}</Text>
                    </View>
                  </TouchableOpacity>

                  {isSelected && plazaDepto && (
                    <View style={styles.plazasConfig}>
                      <View style={styles.toggleContainer}>
                        <TouchableOpacity style={[styles.toggleOption, plazaDepto.esGeneral && styles.toggleOptionSelected]} onPress={() => toggleGeneralDepto(depto.id, true)}>
                          <Text style={[styles.toggleText, plazaDepto.esGeneral && styles.toggleTextSelected]}>👥 General</Text>
                        </TouchableOpacity>
                        <TouchableOpacity style={[styles.toggleOption, !plazaDepto.esGeneral && styles.toggleOptionSelected]} onPress={() => toggleGeneralDepto(depto.id, false)}>
                          <Text style={[styles.toggleText, !plazaDepto.esGeneral && styles.toggleTextSelected]}>🎓 Por ciclo</Text>
                        </TouchableOpacity>
                      </View>

                      {plazaDepto.esGeneral ? (
                        <View style={styles.plazasGeneralesRow}>
                          <Text style={styles.plazasLabel}>Plazas para cualquier ciclo:</Text>
                          <View style={styles.cantidadContainer}>
                            <TouchableOpacity style={styles.cantidadBtn} onPress={() => updatePlazasGenerales(depto.id, plazaDepto.plazasGenerales - 1)}><Ionicons name="remove" size={18} color="#059669" /></TouchableOpacity>
                            <Text style={styles.cantidadText}>{plazaDepto.plazasGenerales}</Text>
                            <TouchableOpacity style={styles.cantidadBtn} onPress={() => updatePlazasGenerales(depto.id, plazaDepto.plazasGenerales + 1)}><Ionicons name="add" size={18} color="#059669" /></TouchableOpacity>
                          </View>
                        </View>
                      ) : (
                        <View style={styles.cursosContainer}>
                          {cursosDepto.map((curso) => {
                            const cursoPlaza = plazaDepto.plazasPorCurso.find(c => c.cursoId === curso.id);
                            const cursoSelected = !!cursoPlaza;
                            return (
                              <View key={curso.id} style={styles.cursoRow}>
                                <TouchableOpacity style={styles.cursoCheckbox} onPress={() => toggleCursoEnDepto(depto.id, curso.id, curso.siglas)}>
                                  <View style={[styles.checkboxSmall, cursoSelected && styles.checkboxSmallSelected]}>
                                    {cursoSelected && <Ionicons name="checkmark" size={12} color="#fff" />}
                                  </View>
                                  <Text style={[styles.cursoSiglasText, cursoSelected && styles.cursoSiglasSelected]}>{curso.siglas}</Text>
                                </TouchableOpacity>
                                {cursoSelected && cursoPlaza && (
                                  <View style={styles.cantidadContainerSmall}>
                                    <TouchableOpacity style={styles.cantidadBtnSmall} onPress={() => updatePlazasCurso(depto.id, curso.id, cursoPlaza.cantidad - 1)}><Ionicons name="remove" size={14} color="#059669" /></TouchableOpacity>
                                    <Text style={styles.cantidadTextSmall}>{cursoPlaza.cantidad}</Text>
                                    <TouchableOpacity style={styles.cantidadBtnSmall} onPress={() => updatePlazasCurso(depto.id, curso.id, cursoPlaza.cantidad + 1)}><Ionicons name="add" size={14} color="#059669" /></TouchableOpacity>
                                  </View>
                                )}
                              </View>
                            );
                          })}
                        </View>
                      )}
                    </View>
                  )}
                </View>
              );
            })}

            {plazasDepartamentos.length > 0 && (
              <View style={styles.resumen}><Text style={styles.resumenText}>Total plazas: {getTotalPlazas()}</Text></View>
            )}
          </View>

          {/* Descripción */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Descripción</Text>
            <TextInput style={[styles.input, styles.textArea]} value={descripcion} onChangeText={setDescripcion} placeholder="Descripción de la empresa..." placeholderTextColor="#999" multiline numberOfLines={4} />
          </View>

          {/* Botón guardar */}
          <TouchableOpacity style={[styles.submitButton, saving && { opacity: 0.7 }]} onPress={handleSubmit} disabled={saving}>
            {saving ? <ActivityIndicator color="#059669" /> : <Text style={styles.submitButtonText}>{isEditing ? 'Guardar cambios' : 'Añadir empresa'}</Text>}
          </TouchableOpacity>

          <View style={{ height: 30 }} />
        </View>
      </ScrollView>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 }, form: { padding: 15 },
  section: { backgroundColor: '#fff', borderRadius: 12, padding: 15, marginBottom: 15, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#333', marginBottom: 5 }, sectionSubtitle: { fontSize: 13, color: '#666', marginBottom: 15 },
  label: { fontSize: 14, fontWeight: '500', color: '#555', marginBottom: 6, marginTop: 12 },
  input: { backgroundColor: '#f8fafc', borderRadius: 10, padding: 14, fontSize: 15, color: '#333', borderWidth: 1, borderColor: '#e2e8f0' }, textArea: { minHeight: 100, textAlignVertical: 'top' },
  deptoContainer: { marginBottom: 12 },
  deptoCard: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#f8fafc', borderRadius: 10, padding: 12, borderWidth: 1, borderColor: '#e2e8f0' },
  deptoCardSelected: { backgroundColor: '#f0fdfa', borderColor: '#0d9488' },
  checkbox: { width: 24, height: 24, borderRadius: 6, backgroundColor: '#e2e8f0', justifyContent: 'center', alignItems: 'center', marginRight: 12 }, checkboxSelected: { backgroundColor: '#0d9488' },
  deptoInfo: { flex: 1 }, deptoCodigo: { fontSize: 14, fontWeight: '700', color: '#333' }, deptoCodigoSelected: { color: '#0d9488' }, deptoNombre: { fontSize: 12, color: '#666' },
  plazasConfig: { marginTop: 10, marginLeft: 36, paddingLeft: 15, borderLeftWidth: 2, borderLeftColor: '#0d9488' },
  toggleContainer: { flexDirection: 'row', backgroundColor: '#f1f5f9', borderRadius: 8, padding: 4, marginBottom: 12 },
  toggleOption: { flex: 1, alignItems: 'center', paddingVertical: 8, borderRadius: 6 }, toggleOptionSelected: { backgroundColor: '#0d9488' },
  toggleText: { fontSize: 13, fontWeight: '500', color: '#666' }, toggleTextSelected: { color: '#fff' },
  plazasGeneralesRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }, plazasLabel: { fontSize: 13, color: '#555' },
  cantidadContainer: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#f1f5f9', borderRadius: 8, borderWidth: 1, borderColor: '#e2e8f0' },
  cantidadBtn: { padding: 8 }, cantidadText: { fontSize: 16, fontWeight: '600', minWidth: 35, textAlign: 'center', color: '#333' },
  cursosContainer: { gap: 8 }, cursoRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  cursoCheckbox: { flexDirection: 'row', alignItems: 'center', flex: 1 },
  checkboxSmall: { width: 20, height: 20, borderRadius: 4, backgroundColor: '#e2e8f0', justifyContent: 'center', alignItems: 'center', marginRight: 10 }, checkboxSmallSelected: { backgroundColor: '#0d9488' },
  cursoSiglasText: { fontSize: 14, color: '#666' }, cursoSiglasSelected: { color: '#0d9488', fontWeight: '500' },
  cantidadContainerSmall: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#f1f5f9', borderRadius: 6, borderWidth: 1, borderColor: '#e2e8f0' },
  cantidadBtnSmall: { padding: 6 }, cantidadTextSmall: { fontSize: 14, fontWeight: '600', minWidth: 25, textAlign: 'center', color: '#333' },
  resumen: { backgroundColor: '#f0fdfa', borderRadius: 8, padding: 12, marginTop: 15, borderWidth: 1, borderColor: '#0d9488' },
  resumenText: { fontSize: 15, fontWeight: '600', color: '#0d9488', textAlign: 'center' },
  submitButton: { backgroundColor: '#fff', paddingVertical: 16, borderRadius: 12, alignItems: 'center', shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4, elevation: 3 },
  submitButtonText: { fontSize: 16, fontWeight: '600', color: '#059669' },
});
