// src/components/GradientBackground.tsx

import React from 'react';
import { StyleSheet } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';

interface GradientBackgroundProps {
  children: React.ReactNode;
}

export default function GradientBackground({ children }: GradientBackgroundProps) {
  return (
   <LinearGradient
     colors={['#3730a3', '#0ea5e9', '#67e8f9']}
     start={{ x: 0, y: 0 }}
     end={{ x: 1, y: 1 }}
     style={styles.gradient}
   >
      {children}
    </LinearGradient>
  );
}

const styles = StyleSheet.create({
  gradient: {
    flex: 1,
  },
});