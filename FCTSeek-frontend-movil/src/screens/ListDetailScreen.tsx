// src/screens/ListDetailScreen.tsx
import React, { useState, useCallback } from 'react';
import {
  View, Text, FlatList, StyleSheet, TouchableOpacity,
  Alert, ActivityIndicator,
} from 'react-native';
import { RouteProp, useRoute, useNavigation, useFocusEffect } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import EmpresaCard from '../components/EmpresaCard';
import { listaService } from '../services/listaService';
import { favoritoService } from '../services/favoritoService';
import { Empresa } from '../types';
import { RootStackParamList } from '../navigation/AppNavigator';

type ListDetailRouteProp = RouteProp<RootStackParamList, 'ListDetail'>;
type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function ListDetailScreen() {
  const route = useRoute<ListDetailRouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { listaId } = route.params;
  const isFavoritos = listaId === -1;

  const [nombre, setNombre] = useState(isFavoritos ? 'Favoritos' : '');
  const [empresas, setEmpresas] = useState<Empresa[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchData = useCallback(async () => {
    try {
      if (isFavoritos) {
        const favs = await favoritoService.getMisFavoritos();
        setEmpresas(favs ?? []);
        setNombre('Favoritos');
      } else {
        const lista = await listaService.getById(listaId);
        setEmpresas(lista?.empresas ?? []);
        setNombre(lista?.nombre ?? 'Lista');
      }
    } catch (error) {
      console.error('Error cargando lista:', error);
      setEmpresas([]);
    } finally {
      setLoading(false);
    }
  }, [listaId, isFavoritos]);

  useFocusEffect(
    useCallback(() => {
      fetchData();
    }, [fetchData])
  );

  if (loading) {
    return (
      <GradientBackground>
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      </GradientBackground>
    );
  }

  const handleEmpresaPress = (empresaId: number) => {
    navigation.navigate('EmpresaDetail', { empresaId });
  };

  const handleRemoveEmpresa = (empresaId: number) => {
    Alert.alert('Eliminar', '¿Quitar esta empresa de la lista?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            if (isFavoritos) {
              await favoritoService.toggle(empresaId);
            } else {
              await listaService.removeEmpresa(listaId, empresaId);
            }
            fetchData();
          } catch (error) {
            Alert.alert('Error', 'No se pudo eliminar');
          }
        },
      },
    ]);
  };

  const handleDeleteList = () => {
    Alert.alert('Eliminar lista', `¿Estás seguro de eliminar "${nombre}"?`, [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Eliminar', style: 'destructive',
        onPress: async () => {
          try {
            await listaService.eliminar(listaId);
            navigation.goBack();
          } catch (error) {
            Alert.alert('Error', 'No se pudo eliminar la lista');
          }
        },
      },
    ]);
  };

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.header}>
          <View style={styles.headerIcon}>
            <Ionicons name={isFavoritos ? 'heart' : 'folder'} size={30} color={isFavoritos ? '#f43f5e' : '#fff'} />
          </View>
          <Text style={styles.headerTitle}>{nombre}</Text>
          <Text style={styles.headerCount}>{empresas.length} empresas</Text>
        </View>

        <FlatList
          data={empresas}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <View style={styles.cardContainer}>
              <EmpresaCard empresa={item} onPress={() => handleEmpresaPress(item.id)} />
              <TouchableOpacity style={styles.removeButton} onPress={() => handleRemoveEmpresa(item.id)}>
                <Ionicons name="close-circle" size={24} color="#f43f5e" />
              </TouchableOpacity>
            </View>
          )}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Ionicons name={isFavoritos ? 'heart-outline' : 'folder-open-outline'} size={60} color="rgba(255,255,255,0.3)" />
              <Text style={styles.emptyText}>
                {isFavoritos ? 'No tienes empresas en favoritos' : 'Esta lista está vacía'}
              </Text>
            </View>
          }
        />

        {!isFavoritos && (
          <TouchableOpacity style={styles.deleteButton} onPress={handleDeleteList}>
            <Ionicons name="trash-outline" size={20} color="#f43f5e" />
            <Text style={styles.deleteText}>Eliminar lista</Text>
          </TouchableOpacity>
        )}
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  header: { alignItems: 'center', paddingVertical: 20 },
  headerIcon: { width: 70, height: 70, borderRadius: 35, backgroundColor: 'rgba(255,255,255,0.15)', justifyContent: 'center', alignItems: 'center', marginBottom: 10 },
  headerTitle: { fontSize: 22, fontWeight: 'bold', color: '#fff' },
  headerCount: { fontSize: 14, color: 'rgba(255,255,255,0.7)', marginTop: 5 },
  list: { paddingBottom: 100 },
  cardContainer: { position: 'relative' },
  removeButton: { position: 'absolute', top: 15, right: 25, backgroundColor: '#fff', borderRadius: 12 },
  emptyContainer: { alignItems: 'center', paddingTop: 60 },
  emptyText: { marginTop: 15, color: 'rgba(255,255,255,0.5)', fontSize: 16, textAlign: 'center', paddingHorizontal: 30 },
  deleteButton: { position: 'absolute', bottom: 20, left: 20, right: 20, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: 'rgba(244,63,94,0.15)', paddingVertical: 14, borderRadius: 12, borderWidth: 1, borderColor: 'rgba(244,63,94,0.3)' },
  deleteText: { color: '#f43f5e', fontSize: 16, fontWeight: '600', marginLeft: 8 },
});
