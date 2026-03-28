import { useState, useEffect, useCallback } from "react";
import ReactECharts from "echarts-for-react";
import { analyticsService } from "../../services/analytics";
import { getUser } from "../../services/auth";
import Header from "../../components/header/Header";
import "./Home.css";

const PRIMARY = "#7c3aed";
const PRIMARY_LIGHT = "#ede9fe";

function formatDate(d) {
    return d.toISOString().split("T")[0];
}

function defaultRange() {
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 30);
    return { start: formatDate(start), end: formatDate(end) };
}

const QUICK_RANGES = [
    {
        label: "This Week",
        getRange() {
            const end = new Date();
            const start = new Date();
            start.setDate(start.getDate() - 6);
            return { start: formatDate(start), end: formatDate(end) };
        },
    },
    {
        label: "This Month",
        getRange() {
            const end = new Date();
            const start = new Date(end.getFullYear(), end.getMonth(), 1);
            return { start: formatDate(start), end: formatDate(end) };
        },
    },
    {
        label: "Last 3 Months",
        getRange() {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 3);
            return { start: formatDate(start), end: formatDate(end) };
        },
    },
    {
        label: "This Year",
        getRange() {
            const end = new Date();
            const start = new Date(end.getFullYear(), 0, 1);
            return { start: formatDate(start), end: formatDate(end) };
        },
    },
    {
        label: "All Time",
        getRange() {
            return { start: "2020-01-01", end: formatDate(new Date()) };
        },
    },
];

const GRANULARITIES = [
    { key: "hourly",  label: "Hour of Day" },
    { key: "weekday", label: "Day of Week" },
    { key: "daily",   label: "By Date" },
    { key: "monthly", label: "By Month" },
];

const BUSY_TIMES_FETCH = {
    hourly:  (s, e) => analyticsService.getBusyTimesHourly(s, e),
    weekday: (s, e) => analyticsService.getBusyTimesWeekday(s, e),
    daily:   (s, e) => analyticsService.getBusyTimesDaily(s, e),
    monthly: (s, e) => analyticsService.getBusyTimesMonthly(s, e),
};

function busyTimesOption(data, granularity) {
    const isLine = granularity === "daily";
    const isAvg = granularity === "hourly" || granularity === "weekday";
    const labels = data.map((d) => d.bucket);
    const values = data.map((d) => d.value);

    return {
        tooltip: { trigger: "axis" },
        grid: { containLabel: true, left: 16, right: 24, top: 16, bottom: 8 },
        xAxis: {
            type: "category",
            data: labels,
            axisLabel: { rotate: granularity === "hourly" ? 45 : 0, fontSize: 11 },
        },
        yAxis: {
            type: "value",
            name: isAvg ? "Avg sessions" : "Sessions",
            nameTextStyle: { fontSize: 11 },
            minInterval: 1,
        },
        series: [{
            type: isLine ? "line" : "bar",
            data: values,
            smooth: true,
            itemStyle: { color: PRIMARY },
            areaStyle: isLine ? { color: PRIMARY_LIGHT, opacity: 0.4 } : undefined,
            lineStyle: isLine ? { color: PRIMARY, width: 2 } : undefined,
        }],
    };
}

function verticalBarOption(names, values, colour = PRIMARY) {
    return {
        tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
        grid: { containLabel: true, left: 16, right: 16, top: 8, bottom: 8 },
        xAxis: {
            type: "category",
            data: names,
            axisLabel: { width: 100, overflow: "truncate", fontSize: 11, rotate: names.length > 5 ? 30 : 0 },
        },
        yAxis: { type: "value", minInterval: 1 },
        series: [{
            type: "bar",
            data: values,
            itemStyle: { color: colour, borderRadius: [4, 4, 0, 0] },
        }],
    };
}

const LoadingState = () => (
    <section className="home-loading">
        <section className="home-spinner" />
    </section>
);

const ErrorState = ({ message }) => (
    <section className="home-error">
        <p>Error loading data</p>
        <p className="home-error-detail">{message}</p>
    </section>
);

const EmptyState = ({ message }) => (
    <section className="home-empty">
        <p>{message}</p>
    </section>
);

const ChartCard = ({ title, loading, error, children }) => (
    <section className="home-card">
        <h2 className="home-card-title">{title}</h2>
        {loading && <LoadingState />}
        {!loading && error && <ErrorState message={error} />}
        {!loading && !error && children}
    </section>
);

const Pill = ({ label, active, onClick }) => (
    <button className={`category-pill ${active ? "active" : ""}`} onClick={onClick}>
        {label}
    </button>
);

export default function Home() {
    const [currentUser, setCurrentUser] = useState(null);
    const [range, setRange] = useState(defaultRange);
    const [granularity, setGranularity] = useState("daily");

    const [busyData, setBusyData] = useState([]);
    const [busyLoading, setBusyLoading] = useState(true);
    const [busyError, setBusyError] = useState(null);

    const [exhibitsData, setExhibitsData] = useState([]);
    const [exhibitsLoading, setExhibitsLoading] = useState(true);
    const [exhibitsError, setExhibitsError] = useState(null);

    const [roomsData, setRoomsData] = useState([]);
    const [roomsLoading, setRoomsLoading] = useState(true);
    const [roomsError, setRoomsError] = useState(null);

    useEffect(() => {
        getUser().then(setCurrentUser).catch(() => {});
    }, []);

    const fetchAll = useCallback((start, end, gran) => {
        setBusyLoading(true);
        setBusyError(null);
        BUSY_TIMES_FETCH[gran](start, end)
            .then(setBusyData)
            .catch((e) => setBusyError(e.message))
            .finally(() => setBusyLoading(false));

        setExhibitsLoading(true);
        setExhibitsError(null);
        analyticsService.getPopularExhibits(start, end)
            .then(setExhibitsData)
            .catch((e) => setExhibitsError(e.message))
            .finally(() => setExhibitsLoading(false));

        setRoomsLoading(true);
        setRoomsError(null);
        analyticsService.getPopularRooms(start, end)
            .then(setRoomsData)
            .catch((e) => setRoomsError(e.message))
            .finally(() => setRoomsLoading(false));
    }, []);

    useEffect(() => {
        fetchAll(range.start, range.end, granularity);
    }, [range, granularity, fetchAll]);

    const beaconedExhibits = exhibitsData.filter((e) => e.unique_visitors !== null);
    const noBeaconCount = exhibitsData.length - beaconedExhibits.length;
    const exhibitNames = beaconedExhibits.map((e) => e.title);
    const exhibitValues = beaconedExhibits.map((e) => e.unique_visitors);
    const roomNames = roomsData.map((r) => r.name);
    const roomValues = roomsData.map((r) => r.unique_visitors);

    return (
        <section className="home">
            <Header title="Analytics Dashboard" currentUser={currentUser} />
            <section className="home-body">

                <section className="home-controls">
                    <section className="home-date-row">
                        <label className="home-date-label">
                            From
                            <input
                                type="date"
                                className="home-date-input"
                                value={range.start}
                                max={range.end}
                                onChange={(e) => setRange((r) => ({ ...r, start: e.target.value }))}
                            />
                        </label>
                        <label className="home-date-label">
                            To
                            <input
                                type="date"
                                className="home-date-input"
                                value={range.end}
                                min={range.start}
                                max={formatDate(new Date())}
                                onChange={(e) => setRange((r) => ({ ...r, end: e.target.value }))}
                            />
                        </label>
                    </section>
                    <section className="home-pills">
                        {QUICK_RANGES.map((q) => (
                            <Pill key={q.label} label={q.label} onClick={() => setRange(q.getRange())} />
                        ))}
                    </section>
                </section>

                <ChartCard title="Visitor Traffic" loading={busyLoading} error={busyError}>
                    <section className="home-pills">
                        {GRANULARITIES.map((g) => (
                            <Pill
                                key={g.key}
                                label={g.label}
                                active={granularity === g.key}
                                onClick={() => setGranularity(g.key)}
                            />
                        ))}
                    </section>
                    {busyData.length === 0
                        ? <EmptyState message="No data for this period." />
                        : <ReactECharts option={busyTimesOption(busyData, granularity)} style={{ height: 300 }} />
                    }
                </ChartCard>

                <ChartCard title="Popular Exhibits" loading={exhibitsLoading} error={exhibitsError}>
                    {beaconedExhibits.length === 0
                        ? <EmptyState message="No exhibit data for this period." />
                        : <>
                            <ReactECharts option={verticalBarOption(exhibitNames, exhibitValues)} style={{ height: 300 }} />
                            {noBeaconCount > 0 && (
                                <p className="home-note">
                                    {noBeaconCount} exhibit{noBeaconCount > 1 ? "s" : ""} not shown — no beacon assigned.
                                </p>
                            )}
                          </>
                    }
                </ChartCard>

                <ChartCard title="Popular Rooms" loading={roomsLoading} error={roomsError}>
                    {roomsData.length === 0
                        ? <EmptyState message="No room data for this period." />
                        : <ReactECharts option={verticalBarOption(roomNames, roomValues, "#0ea5e9")} style={{ height: 300 }} />
                    }
                </ChartCard>

            </section>
        </section>
    );
}
