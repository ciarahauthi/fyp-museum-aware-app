import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
    editFields,
    addFields,
    readOnlyFields,
    updateServices,
    createServices,
    optionLoaders,
    TABLE_SINGULAR,
} from "../manage_content/ManageContentData";
import ConfirmPopup from "../../components/confirm_popup/ConfirmPopup";
import "./Edit.css";

const LABEL_MAP = {
    id: "ID",
    created_at: "Created At",
    updated_at: "Updated At",
    creator_employee_id: "Created By (ID)",
    updated_employee_id: "Last Edited By (ID)",
};

function formatReadOnly(key, value) {
    if (value === null || value === undefined) return "—";
    if (key.endsWith("_at")) return new Date(value).toLocaleString();
    return String(value);
}

export default function Edit() {
    const { state } = useLocation();
    const navigate = useNavigate();

    const { item, tableType } = state || {};
    const isAddMode = !item;
    const fields = isAddMode
        ? addFields[tableType] || []
        : editFields[tableType] || [];
    const roFields = isAddMode ? [] : readOnlyFields[tableType] || [];

    const initialForm = Object.fromEntries(
        fields.map((f) => [
            f.key,
            item?.[f.key] ?? (f.type === "boolean" ? false : ""),
        ]),
    );
    const [form, setForm] = useState(initialForm);
    const [options, setOptions] = useState({});
    const [showConfirm, setShowConfirm] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    // Fetch options for select / image-select fields
    useEffect(() => {
        const optionFields = fields.filter(
            (f) => f.type === "select" || f.type === "image-select",
        );
        const uniqueKeys = [...new Set(optionFields.map((f) => f.optionsKey))];

        uniqueKeys.forEach(async (key) => {
            if (!optionLoaders[key]) return;
            try {
                const data = await optionLoaders[key]();
                setOptions((prev) => ({ ...prev, [key]: data }));
            } catch {
                setOptions((prev) => ({ ...prev, [key]: [] }));
            }
        });
    }, []);

    if (!tableType) {
        return <p className="edit-error">No table selected.</p>;
    }

    const singularLabel = TABLE_SINGULAR[tableType] || tableType;

    const handleChange = (key, value) => {
        setForm((prev) => ({ ...prev, [key]: value }));
    };

    const handleSubmit = async () => {
        setSaving(true);
        setError(null);
        try {
            if (isAddMode) {
                await createServices[tableType](form);
            } else {
                await updateServices[tableType](item.id, form);
            }
            navigate("/manage-content");
        } catch (err) {
            setError(err.message);
            setSaving(false);
            setShowConfirm(false);
        }
    };

    return (
        <section className="edit-page">
            <header className="edit-header">
                <button
                    className="edit-back-btn"
                    onClick={() => navigate("/manage-content")}
                >
                    ← Back
                </button>
                <h1 className="edit-title">
                    {isAddMode ? "Add" : "Edit"} {singularLabel}
                </h1>
            </header>

            <section className="edit-body">
                {/* Read-only info */}
                {roFields.length > 0 && (
                    <section className="edit-readonly-section">
                        <h2 className="edit-section-label">Read-only info</h2>
                        <section className="edit-readonly-grid">
                            {roFields.map((key) => (
                                <section
                                    key={key}
                                    className="edit-readonly-item"
                                >
                                    <span className="edit-readonly-label">
                                        {LABEL_MAP[key] ?? key}
                                    </span>
                                    <span className="edit-readonly-value">
                                        {formatReadOnly(key, item[key])}
                                    </span>
                                </section>
                            ))}
                        </section>
                    </section>
                )}

                {/* Entry Form for both create and update */}
                <section className="edit-form">
                    <h2 className="edit-section-label">
                        {isAddMode ? "New entry" : "Edit fields"}
                    </h2>
                    {fields.map((field) => (
                        <section key={field.key} className="edit-field">
                            <label className="edit-label" htmlFor={field.key}>
                                {field.label}
                            </label>

                            {field.type === "textarea" ? (
                                <textarea
                                    id={field.key}
                                    className="edit-input edit-textarea"
                                    value={form[field.key]}
                                    onChange={(e) =>
                                        handleChange(field.key, e.target.value)
                                    }
                                    rows={4}
                                />
                            ) : field.type === "boolean" ? (
                                <section className="edit-radio-group">
                                    <label className="edit-radio-label">
                                        <input
                                            type="radio"
                                            name={field.key}
                                            value="true"
                                            checked={form[field.key] === true}
                                            onChange={() =>
                                                handleChange(field.key, true)
                                            }
                                        />
                                        Yes
                                    </label>
                                    <label className="edit-radio-label">
                                        <input
                                            type="radio"
                                            name={field.key}
                                            value="false"
                                            checked={form[field.key] === false}
                                            onChange={() =>
                                                handleChange(field.key, false)
                                            }
                                        />
                                        No
                                    </label>
                                </section>
                            ) : field.type === "select" ? (
                                <select
                                    id={field.key}
                                    className="edit-input edit-select"
                                    value={form[field.key]}
                                    onChange={(e) =>
                                        handleChange(
                                            field.key,
                                            Number(e.target.value),
                                        )
                                    }
                                >
                                    <option value="">— Select —</option>
                                    {(options[field.optionsKey] || []).map(
                                        (opt) => (
                                            <option
                                                key={opt[field.valueKey]}
                                                value={opt[field.valueKey]}
                                            >
                                                {opt[field.labelKey]} (ID:{" "}
                                                {opt[field.valueKey]})
                                            </option>
                                        ),
                                    )}
                                </select>
                            ) : field.type === "image-select" ? (
                                <select
                                    id={field.key}
                                    className="edit-input edit-select"
                                    value={form[field.key]}
                                    onChange={(e) =>
                                        handleChange(field.key, e.target.value)
                                    }
                                >
                                    <option value="">— None —</option>
                                    {(options[field.optionsKey] || []).map(
                                        (img) => (
                                            <option
                                                key={img.url}
                                                value={img.url}
                                            >
                                                {img.filename}
                                            </option>
                                        ),
                                    )}
                                </select>
                            ) : field.type === "password" ? (
                                <input
                                    id={field.key}
                                    type="password"
                                    className="edit-input"
                                    value={form[field.key]}
                                    onChange={(e) =>
                                        handleChange(field.key, e.target.value)
                                    }
                                    autoComplete="new-password"
                                />
                            ) : (
                                <input
                                    id={field.key}
                                    type={
                                        field.type === "number"
                                            ? "number"
                                            : "text"
                                    }
                                    className="edit-input"
                                    value={form[field.key]}
                                    onChange={(e) =>
                                        handleChange(
                                            field.key,
                                            field.type === "number"
                                                ? Number(e.target.value)
                                                : e.target.value,
                                        )
                                    }
                                />
                            )}
                        </section>
                    ))}
                </section>

                {error && <p className="edit-error">{error}</p>}

                <button
                    className="edit-save-btn"
                    onClick={() => setShowConfirm(true)}
                >
                    {isAddMode ? `Add ${singularLabel}` : "Save changes"}
                </button>
            </section>

            {showConfirm && (
                <ConfirmPopup
                    message={
                        isAddMode
                            ? `Are you sure you want to add this ${singularLabel}?`
                            : "Are you sure you want to save these changes?"
                    }
                    confirmLabel={
                        isAddMode ? `Add ${singularLabel}` : "Save changes"
                    }
                    saving={saving}
                    onConfirm={handleSubmit}
                    onCancel={() => setShowConfirm(false)}
                />
            )}
        </section>
    );
}
