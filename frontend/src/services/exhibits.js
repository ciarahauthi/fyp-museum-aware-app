import { apiFetch } from "./api";

export const exhibitsService = {
  getAll: () => apiFetch("/api/exhibits/admin"),

  getById: (id) => apiFetch(`/api/exhibits/${id}`),

  create: (data) => apiFetch("/api/exhibits", {
    method: "POST",
    body: JSON.stringify(data),
  }),

  update: (id, data) => apiFetch(`/api/exhibits/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  }),
};