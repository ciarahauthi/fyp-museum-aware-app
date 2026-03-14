import { apiFetch } from "./api";

export const routesService = {
  getAll: () => apiFetch("/api/routes/admin"),

  getLocations: () => apiFetch("/api/routes/locations"),

  create: (data) => apiFetch("/api/routes", {
    method: "POST",
    body: JSON.stringify(data),
  }),

  update: (id, data) => apiFetch(`/api/routes/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  }),
};