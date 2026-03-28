import { apiFetch } from "./api";

export const analyticsService = {
    getBusyTimesHourly: (start, end) =>
        apiFetch(`/api/analytics/busy-times/hourly?start=${start}&end=${end}`),

    getBusyTimesWeekday: (start, end) =>
        apiFetch(`/api/analytics/busy-times/weekday?start=${start}&end=${end}`),

    getBusyTimesDaily: (start, end) =>
        apiFetch(`/api/analytics/busy-times/daily?start=${start}&end=${end}`),

    getBusyTimesMonthly: (start, end) =>
        apiFetch(`/api/analytics/busy-times/monthly?start=${start}&end=${end}`),

    getPopularExhibits: (start, end, limit = 10) =>
        apiFetch(`/api/analytics/popular-exhibits?start=${start}&end=${end}&limit=${limit}`),

    getPopularRooms: (start, end) =>
        apiFetch(`/api/analytics/popular-rooms?start=${start}&end=${end}`),
};
