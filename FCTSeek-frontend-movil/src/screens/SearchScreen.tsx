// src/screens/SearchScreen.tsx
import React, { useState, useEffect, useCallback } from 'react';
import {
  View, Text, TextInput, FlatList, StyleSheet, TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import EmpresaCard from '../components/EmpresaCard';
import { empresaService } from '../services/empresaService';
import { departamentoService } from '../services/departamentoService';
import { Empresa, Departamento } from '../types';
import { RootStackParamList } from '../navigation/AppNavigator';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function SearchScreen() {
  const navigation = useNavigation<NavigationProp>();
  const [busqueda, setBusqueda] = useState('');
  const [deptoSeleccionado, setDeptoSeleccionado] = useState<number | undefined>();
  const [todasEmpresas, setTodasEmpresas] = useState<Empresa[]>([]);
  const [resultados, setResultados] = useState<Empresa[]>([]);
  const [departamentos, setDepartamentos] = useState<Departamento[]>([]);
  const [loading, setLoading] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);

  // Cargar departamentos y empresas al montar
  useEffect(() => {
    const init = async () => {
      try {
        const [deps, emps] = await Promise.all([
          departamentoService.getAll(),
          empresaService.getAll(),
        ]);
        setDepartamentos(deps ?? []);
        setTodasEmpresas(emps ?? []);
        setResultados(emps ?? []);
      } catch (err) {
        console.error('Error inicializando búsqueda:', err);
      } finally {
        setInitialLoad(false);
      }
    };
    init();
  }, []);

  // Filtrar localmente cuando cambia búsqueda o departamento
  useEffect(() => {
    filtrar();
  }, [busqueda, deptoSeleccionado, todasEmpresas]);

  const filtrar = () => {
    let filtered = [...todasEmpresas];

    // Filtrar por texto
    const query = busqueda.trim().toLowerCase();
    if (query.length > 0) {
      filtered = filtered.filter(e =>
        e.nombre.toLowerCase().includes(query) ||
        (e.ciudad && e.ciudad.toLowerCase().includes(query)) ||
        (e.descripcion && e.descripcion.toLowerCase().includes(query))
      );
    }

    // Filtrar por departamento
    if (deptoSeleccionado) {
      filtered = filtered.filter(e =>
        e.departamentos?.some(d => d.id === deptoSeleccionado)
      );
    }

    setResultados(filtered);
  };

  const handleEmpresaPress = (empresaId: number) => {
    navigation.navigate('EmpresaDetail', { empresaId });
  };

  if (initialLoad) {
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
      <View style={styles.container}>
        <View style={styles.searchContainer}>
          <View style={styles.searchBox}>
            <Ionicons name="search" size={20} color="rgba(255,255,255,0.6)" />
            <TextInput
              style={styles.searchInput}
              placeholder="Buscar empresas..."
              placeholderTextColor="rgba(255,255,255,0.5)"
              value={busqueda}
              onChangeText={setBusqueda}
            />
            {busqueda.length > 0 && (
              <TouchableOpacity onPress={() => setBusqueda('')}>
                <Ionicons name="close-circle" size={20} color="rgba(255,255,255,0.6)" />
              </TouchableOpacity>
            )}
          </View>
        </View>

        <View style={styles.filtros}>
          <TouchableOpacity
            style={[styles.filtroChip, !deptoSeleccionado && styles.filtroChipSelected]}
            onPress={() => setDeptoSeleccionado(undefined)}
          >
            <Text style={[styles.filtroText, !deptoSeleccionado && styles.filtroTextSelected]}>Todos</Text>
          </TouchableOpacity>
          {departamentos.map((depto) => (
            <TouchableOpacity
              key={depto.id}
              style={[styles.filtroChip, deptoSeleccionado === depto.id && styles.filtroChipSelected]}
              onPress={() => setDeptoSeleccionado(
                deptoSeleccionado === depto.id ? undefined : depto.id
              )}
            >
              <Text style={[styles.filtroText, deptoSeleccionado === depto.id && styles.filtroTextSelected]}>
                {depto.codigo}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        <Text style={styles.resultadosText}>
          {`${resultados.length} empresa${resultados.length !== 1 ? 's' : ''} encontrada${resultados.length !== 1 ? 's' : ''}`}
        </Text>

        <FlatList
          data={resultados}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <EmpresaCard empresa={item} onPress={() => handleEmpresaPress(item.id)} />
          )}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Ionicons name="search-outline" size={60} color="rgba(255,255,255,0.3)" />
              <Text style={styles.emptyText}>No se encontraron empresas</Text>
            </View>
          }
        />
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  searchContainer: { padding: 15 },
  searchBox: { flexDirection: 'row', alignItems: 'center', backgroundColor: 'rgba(255,255,255,0.15)', borderRadius: 12, paddingHorizontal: 15, borderWidth: 1, borderColor: 'rgba(255,255,255,0.2)' },
  searchInput: { flex: 1, padding: 12, fontSize: 16, color: '#fff' },
  filtros: { flexDirection: 'row', paddingHorizontal: 15, marginBottom: 10, gap: 8, flexWrap: 'wrap' },
  filtroChip: { paddingHorizontal: 16, paddingVertical: 8, borderRadius: 20, backgroundColor: 'rgba(255,255,255,0.15)', borderWidth: 1, borderColor: 'rgba(255,255,255,0.2)' },
  filtroChipSelected: { backgroundColor: '#fff' },
  filtroText: { fontSize: 13, fontWeight: '500', color: 'rgba(255,255,255,0.9)' },
  filtroTextSelected: { color: '#059669' },
  resultadosText: { paddingHorizontal: 15, marginBottom: 10, color: 'rgba(255,255,255,0.7)', fontSize: 14 },
  list: { paddingBottom: 20 },
  emptyContainer: { alignItems: 'center', paddingTop: 60 },
  emptyText: { marginTop: 15, color: 'rgba(255,255,255,0.5)', fontSize: 16 },
});
