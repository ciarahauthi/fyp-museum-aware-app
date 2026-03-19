import { useState, useEffect } from "react";
import "./Settings.css";
import Header from "../../components/header/Header";
import { getUser } from "../../services/auth";
import { changesService } from "../../services/changes";

const PREVIEW_COUNT = 10;

function formatDetails(details) {
    if (!details) return "—";
    const { changes, ...rest } = details;
    const parts = Object.entries(rest).map(([k, v]) => `${k}: ${v}`);
    if (changes) {
        const changed = Object.keys(changes).join(", ");
        if (changed) parts.push(`changed: ${changed}`);
    }
    return parts.join(" · ");
}

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString(undefined, {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

function ChangelogEntry({ entry }) {
    const badgeClass = entry.change.toLowerCase();
    return (
        <section className="changelog-entry">
            <section className={`changelog-pill ${badgeClass}`}>{entry.change}</section>
            <section className="changelog-table">{entry.table_name}</section>
            <section className="changelog-details">{formatDetails(entry.details)}</section>
            <section className="changelog-user">user id: {entry.employee_id ?? "—"}</section>
            <section className="changelog-time">{formatTime(entry.timestamp)}</section>
        </section>
    );
}

export default function Settings() {
    const [currentUser, setCurrentUser] = useState(null);
    const [changes, setChanges] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [expanded, setExpanded] = useState(false);

    useEffect(() => {
        getUser()
            .then((u) => setCurrentUser(u))
            .catch(() => {});
    }, []);

    useEffect(() => {
        changesService
            .getAll()
            .then((data) => setChanges(data))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    const visible = expanded ? changes : changes.slice(0, PREVIEW_COUNT);
    const hasMore = changes.length > PREVIEW_COUNT;

    return (
        <section className="settings">
            <Header title="Settings" currentUser={currentUser} />
            <section className="settings-body">
                <section className="settings-tile">
                    <section className="settings-tile-header">
                        <section>
                            <p className="settings-tile-title">Change Log</p>
                            {!loading && !error && (
                                <p className="settings-tile-subtitle">
                                    {changes.length} total entries
                                </p>
                            )}
                        </section>
                        {hasMore && (
                            <button
                                className="settings-expand-btn"
                                onClick={() => setExpanded((e) => !e)}
                            >
                                {expanded ? "Show less" : `Show all ${changes.length}`}
                            </button>
                        )}
                    </section>

                    {loading && (
                        <div className="settings-loading">
                            <div className="settings-spinner" />
                        </div>
                    )}
                    {error && <p className="settings-error">Failed to load changes: {error}</p>}
                    {!loading && !error && changes.length === 0 && (
                        <p className="settings-empty">No changes recorded yet.</p>
                    )}
                    {!loading && !error && changes.length > 0 && (
                        <section className="changelog-list">
                            {visible.map((entry) => (
                                <ChangelogEntry key={entry.id} entry={entry} />
                            ))}
                        </section>
                    )}
                </section>
            </section>
        </section>
    );
}
