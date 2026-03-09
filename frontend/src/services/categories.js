import { apiFetch } from "./api";

export const categoriesService = {
  getAll: () => apiFetch("/api/categories"),

  getById: (id) => apiFetch(`/api/categories/${id}`),
};