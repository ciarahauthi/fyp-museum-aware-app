import { apiFetch } from "./api";

export const categoriesService = {
  getAll: () => apiFetch("/api/categories"),

  getById: (id) => apiFetch(`/api/categories/${id}`),

  update: (id, data) => apiFetch(`/api/categories/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  }),
};