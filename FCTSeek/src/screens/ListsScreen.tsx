import React, { useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  Modal,
  TextInput,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import { listas } from '../data/mockData';
import { RootStackParamList } from '../navigation/AppNavigator';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function ListsScreen() {
  const navigation = useNavigation<NavigationProp>();
  const [modalVisible, setModalVisible] = useState(false);
  const [nuevaLista, setNuevaLista] = useState('');

  const handleListPress = (listaId: number) => {
    navigation.navigate('ListDetail', { listaId });
  };

  const handleCrearLista = () => {
    if (nuevaLista.trim()) {
      console.log('Crear lista:', nuevaLista);
      setNuevaLista('');
      setModalVisible(false);
    }
  };

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Mis Listas</Text>
          <TouchableOpacity style={styles.addButton} onPress={() => setModalVisible(true)}>
            <Ionicons name="add" size={24} color="#fff" />
          </TouchableOpacity>
        </View>

        <FlatList
          data={listas}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <TouchableOpacity style={styles.listaCard} onPress={() => handleListPress(item.id)}>
              <View style={styles.listaIcon}>
                <Ionicons
                  name={item.esFavoritos ? 'heart' : 'folder'}
                  size={24}
                  color={item.esFavoritos ? '#f43f5e' : '#fff'}
                />
              </View>
              <View style={styles.listaInfo}>
                <Text style={styles.listaNombre}>{item.nombre}</Text>
                <Text style={styles.listaCount}>{item.empresas.length} empresas</Text>
              </View>
              <Ionicons name="chevron-forward" size={20} color="rgba(255,255,255,0.5)" />
            </TouchableOpacity>
          )}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
        />

        {/* Modal crear lista */}
        <Modal
          animationType="fade"
          transparent={true}
          visible={modalVisible}
          onRequestClose={() => setModalVisible(false)}
        >
          <View style={styles.modalOverlay}>
            <View style={styles.modalContent}>
              <View style={styles.modalHeader}>
                <Text style={styles.modalTitle}>Nueva lista</Text>
                <TouchableOpacity onPress={() => setModalVisible(false)}>
                  <Ionicons name="close" size={24} color="#666" />
                </TouchableOpacity>
              </View>
              <TextInput
                style={styles.modalInput}
                placeholder="Nombre de la lista"
                placeholderTextColor="#999"
                value={nuevaLista}
                onChangeText={setNuevaLista}
                maxLength={50}
              />
              <View style={styles.modalButtons}>
                <TouchableOpacity style={styles.modalButtonCancel} onPress={() => setModalVisible(false)}>
                  <Text style={styles.modalButtonCancelText}>Cancelar</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.modalButtonConfirm, !nuevaLista.trim() && styles.modalButtonDisabled]}
                  onPress={handleCrearLista}
                  disabled={!nuevaLista.trim()}
                >
                  <Text style={styles.modalButtonConfirmText}>Crear</Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        </Modal>
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 20 },
  headerTitle: { fontSize: 24, fontWeight: 'bold', color: '#fff' },
  addButton: { width: 45, height: 45, borderRadius: 12, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center' },
  list: { paddingHorizontal: 15, paddingBottom: 20 },
  listaCard: { flexDirection: 'row', alignItems: 'center', backgroundColor: 'rgba(255,255,255,0.1)', borderRadius: 12, padding: 15, marginBottom: 10, borderWidth: 1, borderColor: 'rgba(255,255,255,0.15)' },
  listaIcon: { width: 50, height: 50, borderRadius: 12, backgroundColor: 'rgba(255,255,255,0.15)', justifyContent: 'center', alignItems: 'center', marginRight: 15 },
  listaInfo: { flex: 1 },
  listaNombre: { fontSize: 16, fontWeight: '600', color: '#fff' },
  listaCount: { fontSize: 13, color: 'rgba(255,255,255,0.7)', marginTop: 2 },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', alignItems: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 16, padding: 20, width: '100%', maxWidth: 400 },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  modalTitle: { fontSize: 18, fontWeight: '600', color: '#333' },
  modalInput: { backgroundColor: '#f5f5f5', borderRadius: 10, padding: 14, fontSize: 15, borderWidth: 1, borderColor: '#e0e0e0', color: '#333' },
  modalButtons: { flexDirection: 'row', marginTop: 20, gap: 10 },
  modalButtonCancel: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#f5f5f5', alignItems: 'center' },
  modalButtonCancelText: { fontSize: 15, fontWeight: '600', color: '#666' },
  modalButtonConfirm: { flex: 1, padding: 14, borderRadius: 10, backgroundColor: '#059669', alignItems: 'center' },
  modalButtonDisabled: { backgroundColor: '#ccc' },
  modalButtonConfirmText: { fontSize: 15, fontWeight: '600', color: '#fff' },
});