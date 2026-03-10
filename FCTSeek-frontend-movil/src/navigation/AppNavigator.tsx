import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Ionicons } from '@expo/vector-icons';
import { LinearGradient } from 'expo-linear-gradient';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { useAuth } from '../context/AuthContext';

import LoginScreen from '../screens/LoginScreen';
import HomeScreen from '../screens/HomeScreen';
import SearchScreen from '../screens/SearchScreen';
import ListsScreen from '../screens/ListsScreen';
import ProfileScreen from '../screens/ProfileScreen';
import EmpresaDetailScreen from '../screens/EmpresaDetailScreen';
import AddEmpresaScreen from '../screens/AddEmpresaScreen';
import ListDetailScreen from '../screens/ListDetailScreen';

import { colors } from '../theme/colors';

const GRADIENT_COLORS = ['#3730a3', '#0ea5e9', '#67e8f9'];

const GradientHeader = () => (
  <LinearGradient
    colors={GRADIENT_COLORS}
    start={{ x: 0, y: 0 }}
    end={{ x: 1, y: 0 }}
    style={{ flex: 1 }}
  />
);

export type RootStackParamList = {
  Login: undefined;
  MainTabs: undefined;
  EmpresaDetail: { empresaId: number };
  AddEmpresa: undefined;
  EditEmpresa: { empresaId: number };
  ListDetail: { listaId: number };
};

export type TabParamList = {
  Inicio: undefined;
  Buscar: undefined;
  Listas: undefined;
  Perfil: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<TabParamList>();

function MainTabs() {
  const insets = useSafeAreaInsets();
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap;
          switch (route.name) {
            case 'Inicio': iconName = focused ? 'home' : 'home-outline'; break;
            case 'Buscar': iconName = focused ? 'search' : 'search-outline'; break;
            case 'Listas': iconName = focused ? 'list' : 'list-outline'; break;
            case 'Perfil': iconName = focused ? 'person' : 'person-outline'; break;
            default: iconName = 'help-outline';
          }
          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#3730a3',
        tabBarInactiveTintColor: colors.tabInactive,
        tabBarStyle: {
          backgroundColor: colors.surface,
          borderTopColor: colors.border,
          paddingBottom: insets.bottom + 5,
          paddingTop: 5,
          height: 60 + insets.bottom,
        },
        tabBarLabelStyle: { fontSize: 12, fontWeight: '500' },
        headerBackground: () => <GradientHeader />,
        headerTintColor: '#fff',
        headerTitleStyle: { fontWeight: 'bold', color: '#fff' },
      })}
    >
      <Tab.Screen name="Inicio" component={HomeScreen} options={{ title: 'FCT-Seek' }} />
      <Tab.Screen name="Buscar" component={SearchScreen} options={{ title: 'Buscar Empresas' }} />
      <Tab.Screen name="Listas" component={ListsScreen} options={{ title: 'Mis Listas' }} />
      <Tab.Screen name="Perfil" component={ProfileScreen} options={{ title: 'Mi Perfil' }} />
    </Tab.Navigator>
  );
}

export default function AppNavigator() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#3730a3' }}>
        <ActivityIndicator size="large" color="#fff" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!isAuthenticated ? (
          <Stack.Screen name="Login" component={LoginScreen} />
        ) : (
          <>
            <Stack.Screen name="MainTabs" component={MainTabs} />
            <Stack.Screen name="AddEmpresa" component={AddEmpresaScreen}
              options={{
                headerShown: true,
                headerBackground: () => <GradientHeader />,
                headerTintColor: '#fff',
                headerTitleStyle: { fontWeight: 'bold', color: '#fff' },
                title: 'Añadir Empresa',
              }} />
            <Stack.Screen name="EditEmpresa" component={AddEmpresaScreen}
              options={{
                headerShown: true,
                headerBackground: () => <GradientHeader />,
                headerTintColor: '#fff',
                headerTitleStyle: { fontWeight: 'bold', color: '#fff' },
                title: 'Editar Empresa',
              }} />
            <Stack.Screen name="EmpresaDetail" component={EmpresaDetailScreen}
              options={{
                headerShown: true,
                headerBackground: () => <GradientHeader />,
                headerTintColor: '#fff',
                headerTitleStyle: { fontWeight: 'bold', color: '#fff' },
                title: 'Detalle',
              }} />
            <Stack.Screen name="ListDetail" component={ListDetailScreen}
              options={{
                headerShown: true,
                headerBackground: () => <GradientHeader />,
                headerTintColor: '#fff',
                headerTitleStyle: { fontWeight: 'bold', color: '#fff' },
                title: 'Mi Lista',
              }} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
