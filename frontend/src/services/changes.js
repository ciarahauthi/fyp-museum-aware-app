import { apiFetch } from "./api";

export const changesService = {
    getAll: () => apiFetch("/api/changes"),
};
