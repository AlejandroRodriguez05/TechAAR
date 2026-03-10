// src/config/constants.ts
import Constants from 'expo-constants';

// En desarrollo, Expo sabe la IP de tu PC porque se conecta a ella.
// En producción, pon aquí la URL real del servidor.

const PRODUCTION_URL = 'https://tu-servidor.com/api';
const PORT = '8080';

function getBaseUrl(): string {
  if (!__DEV__) {
    return PRODUCTION_URL;
  }

  // Expo proporciona la IP del PC en debuggerHost (formato "192.168.1.100:8081")
  const debuggerHost =
    Constants.expoConfig?.hostUri ??
    Constants.manifest2?.extra?.expoGo?.debuggerHost ??
    Constants.manifest?.debuggerHost;

  if (debuggerHost) {
    const ip = debuggerHost.split(':')[0];
    return `http://${ip}:${PORT}/api`;
  }

  return `http://localhost:${PORT}/api`;
}

export const API_BASE_URL = getBaseUrl();
