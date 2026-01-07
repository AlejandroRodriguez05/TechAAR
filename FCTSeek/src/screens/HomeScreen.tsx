import React from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';

import GradientBackground from '../components/GradientBackground';
import EmpresaCard from '../components/EmpresaCard';
import { empresas, currentUser } from '../data/mockData';
import { RootStackParamList } from '../navigation/AppNavigator';

type NavigationProp = NativeStackNavigationProp<RootStackParamList>;

export default function HomeScreen() {
  const navigation = useNavigation<NavigationProp>();

  const handleEmpresaPress = (empresaId: number) => {
    navigation.navigate('EmpresaDetail', { empresaId });
  };

  const handleAddEmpresa = () => {
    navigation.navigate('AddEmpresa');
  };

  return (
    <GradientBackground>
      <View style={styles.container}>
        <View style={styles.greeting}>
          <View style={styles.greetingRow}>
            <View>
              <Text style={styles.greetingText}>
                Hola, {currentUser.nombre} 👋
              </Text>
              <Text style={styles.greetingSubtext}>
                {currentUser.rol === 'PROFESOR' ? 'Profesor' : 'Alumno'} • {empresas.length} empresas disponibles
              </Text>
            </View>

            {currentUser.rol === 'PROFESOR' && (
              <TouchableOpacity style={styles.addButton} onPress={handleAddEmpresa}>
                <Ionicons name="add" size={24} color="#fff" />
              </TouchableOpacity>
            )}
          </View>
        </View>

        <View style={styles.quickActions}>
          <TouchableOpacity style={styles.quickAction}>
            <View style={styles.quickIcon}>
              <Ionicons name="business-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Nuevas</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.quickAction}>
            <View style={styles.quickIcon}>
              <Ionicons name="star-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Top valoradas</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.quickAction}>
            <View style={styles.quickIcon}>
              <Ionicons name="location-outline" size={20} color="#fff" />
            </View>
            <Text style={styles.quickText}>Cercanas</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.listHeader}>
          <Text style={styles.listTitle}>Todas las empresas</Text>
          <Text style={styles.listCount}>{empresas.length}</Text>
        </View>

        <FlatList
          data={empresas}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <EmpresaCard empresa={item} onPress={() => handleEmpresaPress(item.id)} />
          )}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
        />
      </View>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  greeting: { paddingHorizontal: 20, paddingTop: 15, paddingBottom: 10 },
  greetingRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  greetingText: { fontSize: 22, fontWeight: 'bold', color: '#fff' },
  greetingSubtext: { fontSize: 14, color: 'rgba(255,255,255,0.8)', marginTop: 2 },
  addButton: { width: 45, height: 45, borderRadius: 12, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center' },
  quickActions: { flexDirection: 'row', justifyContent: 'space-around', paddingVertical: 15, paddingHorizontal: 10 },
  quickAction: { alignItems: 'center' },
  quickIcon: { width: 50, height: 50, borderRadius: 15, backgroundColor: 'rgba(255,255,255,0.2)', justifyContent: 'center', alignItems: 'center', marginBottom: 6 },
  quickText: { fontSize: 12, color: 'rgba(255,255,255,0.9)', fontWeight: '500' },
  listHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, paddingVertical: 10 },
  listTitle: { fontSize: 18, fontWeight: '600', color: '#fff' },
  listCount: { fontSize: 14, color: '#fff', backgroundColor: 'rgba(255,255,255,0.2)', paddingHorizontal: 10, paddingVertical: 4, borderRadius: 10 },
  list: { paddingBottom: 20 },
});