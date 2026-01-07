import React from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  Alert,
} from 'react-native';
import { RouteProp, useRoute, useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import EmpresaCard from '../components/EmpresaCard';
import { listas } from '../data/mockData';
import { RootStackParamList } from '../navigation/AppNavigator';

type ListDetailRouteProp = RouteProp<RootStackParamList, 'ListDetail'>;
type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function ListDetailScreen() {
  const route = useRoute<ListDetailRouteProp>();
  const navigation = useNavigation<NavigationProp>();
  const { listaId } = route.params;

  const lista = listas.find(l => l.id === listaId);

  if (!lista) {
    return (
      <GradientBackground>
        <View style={styles.container}>
          <Text style={styles.errorText}>Lista no encontrada</Text>
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
      { text: 'Eliminar', style: 'destructive', onPress: () => console.log('Eliminar:', empresaId) },
    ]);
  };

  const handleDeleteList = () => {
    Alert.alert('Eliminar lista', `¿Estás seguro de eliminar "${lista.nombre}"?`, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Eliminar', style: 'destructive', onPress: () => navigation.goBack() },
    ]);
  };

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.header}>
          <View style={styles.headerIcon}>
            <Ionicons name={lista.esFavoritos ? 'heart' : 'folder'} size={30} color={lista.esFavoritos ? '#f43f5e' : '#fff'} />
          </View>
          <Text style={styles.headerTitle}>{lista.nombre}</Text>
          <Text style={styles.headerCount}>{lista.empresas.length} empresas</Text>
        </View>

        <FlatList
          data={lista.empresas}
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
              <Ionicons name="folder-open-outline" size={60} color="rgba(255,255,255,0.3)" />
              <Text style={styles.emptyText}>Esta lista está vacía</Text>
            </View>
          }
        />

        {!lista.esFavoritos && (
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
  emptyText: { marginTop: 15, color: 'rgba(255,255,255,0.5)', fontSize: 16 },
  deleteButton: { position: 'absolute', bottom: 20, left: 20, right: 20, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: 'rgba(244,63,94,0.15)', paddingVertical: 14, borderRadius: 12, borderWidth: 1, borderColor: 'rgba(244,63,94,0.3)' },
  deleteText: { color: '#f43f5e', fontSize: 16, fontWeight: '600', marginLeft: 8 },
  errorText: { color: '#fff', fontSize: 16, textAlign: 'center', marginTop: 50 },
});