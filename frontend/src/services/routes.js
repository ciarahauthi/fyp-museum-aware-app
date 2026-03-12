import { apiFetch } from "./api";

export const routesService = {
  getAll: () => apiFetch("/api/routes"),

  getLocations: () => apiFetch("/api/routes/locations"),

  update: (id, data) => apiFetch(`/api/routes/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  }),
};