// src/components/EmpresaCard.tsx

import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

import { Empresa } from '../types';
import { colors } from '../theme/colors';

interface EmpresaCardProps {
  empresa: Empresa;
  onPress: () => void;
}

export default function EmpresaCard({ empresa, onPress }: EmpresaCardProps) {
  const tieneContactos = empresa.contactadaPor && empresa.contactadaPor.length > 0;

  return (
    <TouchableOpacity style={styles.card} onPress={onPress}>
      <View style={styles.header}>
        <Text style={styles.nombre} numberOfLines={1}>{empresa.nombre}</Text>
        {empresa.valoracionMedia && (
          <View style={styles.valoracion}>
            <Ionicons name="star" size={14} color={colors.star} />
            <Text style={styles.valoracionText}>{empresa.valoracionMedia.toFixed(1)}</Text>
          </View>
        )}
      </View>

      <View style={styles.ubicacion}>
        <Ionicons name="location-outline" size={14} color={colors.textSecondary} />
        <Text style={styles.ubicacionText}>{empresa.ciudad}</Text>
      </View>

      {/* Departamentos de la empresa */}
      <View style={styles.tags}>
        {(empresa.departamentos ?? []).map((dept) => (
          <View key={dept.id} style={styles.tag}>
            <Text style={styles.tagText}>{dept.codigo}</Text>
          </View>
        ))}
      </View>

      {/* Contactado por - colores suaves */}
      {tieneContactos && (
        <View style={styles.contactadoContainer}>
          <View style={styles.contactadoHeader}>
            <Ionicons name="checkmark-circle" size={14} color="#059669" />
            <Text style={styles.contactadoLabel}>Contactado por:</Text>
          </View>
          <View style={styles.contactadoDeptos}>
            {empresa.contactadaPor!.map((contacto, index) => (
              <View key={index} style={styles.contactadoDepto}>
                <Text style={styles.contactadoDeptoText}>{contacto.departamentoNombre}</Text>
              </View>
            ))}
          </View>
        </View>
      )}

    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 12,
    padding: 15,
    marginHorizontal: 15,
    marginVertical: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  nombre: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.textPrimary,
    flex: 1,
    marginRight: 10,
  },
  valoracion: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fef9c3',
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
  },
  valoracionText: {
    fontSize: 13,
    fontWeight: '600',
    color: colors.textPrimary,
    marginLeft: 3,
  },
  ubicacion: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  ubicacionText: {
    fontSize: 13,
    color: colors.textSecondary,
    marginLeft: 4,
  },
  tags: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 8,
    gap: 6,
  },
  tag: {
    backgroundColor: colors.primary,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 6,
  },
  tagText: {
    color: '#fff',
    fontSize: 11,
    fontWeight: '600',
  },

  // Contactado por - colores suaves verdes
  contactadoContainer: {
    backgroundColor: '#f0fdf4',  // Verde muy claro
    borderRadius: 8,
    padding: 10,
    marginBottom: 10,
    borderLeftWidth: 3,
    borderLeftColor: '#10b981',  // Verde esmeralda
  },
  contactadoHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 6,
  },
  contactadoLabel: {
    fontSize: 12,
    fontWeight: '600',
    color: '#059669',  // Verde oscuro legible
    marginLeft: 5,
  },
  contactadoDeptos: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 6,
  },
  contactadoDepto: {
    backgroundColor: '#dcfce7',  // Verde claro
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  contactadoDeptoText: {
    fontSize: 12,
    color: '#166534',  // Verde oscuro para buena legibilidad
    fontWeight: '500',
  },

});