/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_GATEWAY_URL: string
  readonly VITE_GRAFANA_URL: string
  readonly VITE_KEYCLOAK_URL: string
  readonly VITE_KEYCLOAK_CLIENT_ID: string
  readonly VITE_USE_MOCK_AUTH: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
