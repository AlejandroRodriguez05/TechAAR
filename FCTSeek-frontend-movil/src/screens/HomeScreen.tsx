// src/screens/HomeScreen.tsx
import React, { useState, useCallback } from 'react';
import {
  View, Text, FlatList, StyleSheet, TouchableOpacity,
  ActivityIndicator, RefreshControl, Alert,
} from 'react-native';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import EmpresaCard from '../components/EmpresaCard';
import { useAuth } from '../context/AuthContext';
import { empresaService } from '../services/empresaService';
import { Empresa } from '../types';
import { RootStackParamList } from '../navigation/AppNavigator';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;
type Filtro = 'todas' | 'nuevas' | 'top' | 'cercanas';

export default function HomeScreen() {
  const navigation = useNavigation<NavigationProp>();
  const { user } = useAuth();

  const [empresas, setEmpresas] = useState<Empresa[]>([]);
  const [filtro, setFiltro] = useState<Filtro>('todas');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchEmpresas = useCallback(async () => {
    try {
      const data = await empresaService.getAll();
      setEmpresas(data ?? []);
    } catch (error) {
      console.error('Error cargando empresas:', error);
      setEmpresas([]);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      fetchEmpresas();
    }, [fetchEmpresas])
  );

  const onRefresh = () => {
    setRefreshing(true);
    fetchEmpresas();
  };

  // Filtrar empresas en el cliente
  const empresasFiltradas = (() => {
    switch (filtro) {
      case 'top':
        return [...empresas]
          .filter(e => e.valoracionMedia != null && e.valoracionMedia > 0)
          .sort((a, b) => (b.valoracionMedia ?? 0) - (a.valoracionMedia ?? 0));
      case 'nuevas':
        // Las más recientes (por ID descendente como aproximación)
        return [...empresas].sort((a, b) => b.id - a.id);
      case 'cercanas':
        // Sin geolocalización, mostrar todas con aviso
        return empresas;
      default:
        return empresas;
    }
  })();

  const handleEmpresaPress = (empresaId: number) => {
    navigation.navigate('EmpresaDetail', { empresaId });
  };

  const handleAddEmpresa = () => {
    navigation.navigate('AddEmpresa');
  };

  const handleFiltro = (nuevoFiltro: Filtro) => {
    setFiltro(nuevoFiltro);
    if (nuevoFiltro === 'cercanas') {
      Alert.alert('Próximamente', 'La función de empresas cercanas estará disponible en futuras versiones.');
    }
  };

  const esProfesor = user?.rol === 'PROFESOR';

  if (loading) {
    return (
      <GradientBackground>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </GradientBackground>
    );
  }

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.greeting}>
          <View style={styles.greetingRow}>
            <View style={{ flex: 1 }}>
              <Text style={styles.greetingText}>
                Hola, {user?.nombre || 'Usuario'} 👋
              </Text>
              <Text style={styles.greetingSubtext}>
                {esProfesor ? 'Profesor' : 'Alumno'} • {empresas.length} empresas
              </Text>
            </View>

            {esProfesor && (
              <TouchableOpacity style={styles.addButton} onPress={handleAddEmpresa}>
                <Ionicons name="add" size={24} color="#fff" />
              </TouchableOpacity>
            )}
          </View>
        </View>

        <View style={styles.quickActions}>
          <TouchableOpacity
            style={[styles.quickAction, filtro === 'nuevas' && styles.quickActionActive]}
            onPress={() => handleFiltro(filtro === 'nuevas' ? 'todas' : 'nuevas')}
          >
            <View style={[styles.quickIcon, filtro === 'nuevas' && styles.quickIconActive]}>
              <Ionicons name="business-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Nuevas</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.quickAction, filtro === 'top' && styles.quickActionActive]}
            onPress={() => handleFiltro(filtro === 'top' ? 'todas' : 'top')}
          >
            <View style={[styles.quickIcon, filtro === 'top' && styles.quickIconActive]}>
              <Ionicons name="star-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Top valoradas</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.quickAction, filtro === 'cercanas' && styles.quickActionActive]}
            onPress={() => handleFiltro('cercanas')}
          >
            <View style={styles.quickIcon}>
              <Ionicons name="location-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Cercanas</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.listHeader}>
          <Text style={styles.listTitle}>
            {filtro === 'todas' ? 'Todas las empresas' :
             filtro === 'top' ? 'Mejor valoradas' :
             filtro === 'nuevas' ? 'Más recientes' : 'Todas las empresas'}
          </Text>
          <Text style={styles.listCount}>{empresasFiltradas.length}</Text>
        </View>

        <FlatList
          data={empresasFiltradas}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <EmpresaCard empresa={item} onPress={() => handleEmpresaPress(item.id)} />
          )}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#fff" />
          }
        />
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  loadingContainer: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  greeting: { paddingHorizontal: 20, paddingTop: 15, paddingBottom: 10 },
  greetingRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  greetingText: { fontSize: 22, fontWeight: 'bold', color: '#fff' },
  greetingSubtext: { fontSize: 14, color: 'rgba(255,255,255,0.8)', marginTop: 2 },
  addButton: { width: 45, height: 45, borderRadius: 12, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center' },
  quickActions: { flexDirection: 'row', justifyContent: 'space-around', paddingVertical: 15, paddingHorizontal: 10 },
  quickAction: { alignItems: 'center' },
  quickActionActive: { opacity: 1 },
  quickIcon: { width: 50, height: 50, borderRadius: 15, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center', marginBottom: 6 },
  quickIconActive: { backgroundColor: 'rgba(255,255,255,0.4)' },
  quickText: { fontSize: 12, color: 'rgba(255,255,255,0.9)', fontWeight: '500' },
  listHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, paddingVertical: 10 },
  listTitle: { fontSize: 18, fontWeight: '600', color: '#fff' },
  listCount: { fontSize: 14, color: '#fff', backgroundColor: 'rgba(255,255,255,0.2)', paddingHorizontal: 10, paddingVertical: 4, borderRadius: 10 },
  list: { paddingBottom: 20 },
});
