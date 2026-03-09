import { apiFetch } from "./api";

export const routesService = {
  getAll: () => apiFetch("/api/routes"),
};