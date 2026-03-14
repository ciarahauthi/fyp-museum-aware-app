import { apiFetch } from "./api";

const API_BASE = import.meta.env.VITE_API_BASE;

export const imagesService = {
  getImages: (folder) => apiFetch(`/api/images/${folder}`),

  upload: async (folder, file) => {
    const token = localStorage.getItem("token");
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${API_BASE}/api/images/${folder}`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      throw new Error(err.detail || `HTTP ${response.status}`);
    }

    return response.json();
  },
};
