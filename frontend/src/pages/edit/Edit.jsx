import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { editFields, readOnlyFields, updateServices, optionLoaders } from "../manage_content/ManageContentData";
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
  const fields = editFields[tableType] || [];
  const roFields = readOnlyFields[tableType] || [];

  const initialForm = Object.fromEntries(fields.map((f) => [f.key, item?.[f.key] ?? ""]));
  const [form, setForm] = useState(initialForm);
  const [options, setOptions] = useState({});
  const [showConfirm, setShowConfirm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Fetch options for any select fields
  useEffect(() => {
    const selectFields = fields.filter((f) => f.type === "select");
    const uniqueKeys = [...new Set(selectFields.map((f) => f.optionsKey))];

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

  if (!item || !tableType) {
    return <p className="edit-error">No item to edit.</p>;
  }

  const handleChange = (key, value) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    setSaving(true);
    setError(null);
    try {
      await updateServices[tableType](item.id, form);
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
        <button className="edit-back-btn" onClick={() => navigate("/manage-content")}>← Back</button>
        <h1 className="edit-title">Edit {tableType.slice(0, -1)}</h1>
      </header>

      <section className="edit-body">
        {/* Read-only info */}
        {roFields.length > 0 && (
          <section className="edit-readonly-section">
            <h2 className="edit-section-label">Read-only info</h2>
            <section className="edit-readonly-grid">
              {roFields.map((key) => (
                <section key={key} className="edit-readonly-item">
                  <span className="edit-readonly-label">{LABEL_MAP[key] ?? key}</span>
                  <span className="edit-readonly-value">{formatReadOnly(key, item[key])}</span>
                </section>
              ))}
            </section>
          </section>
        )}

        {/* Edit form */}
        <section className="edit-form">
          <h2 className="edit-section-label">Edit fields</h2>
          {fields.map((field) => (
            <section key={field.key} className="edit-field">
              <label className="edit-label" htmlFor={field.key}>{field.label}</label>

              {field.type === "textarea" ? (
                <textarea
                  id={field.key}
                  className="edit-input edit-textarea"
                  value={form[field.key]}
                  onChange={(e) => handleChange(field.key, e.target.value)}
                  rows={4}
                />
              ) : field.type === "boolean" ? (
                <section className="edit-toggle-row">
                  <input
                    id={field.key}
                    type="checkbox"
                    className="edit-checkbox"
                    checked={!!form[field.key]}
                    onChange={(e) => handleChange(field.key, e.target.checked)}
                  />
                  <label htmlFor={field.key} className="edit-toggle-label">
                    {form[field.key] ? "Yes" : "No"}
                  </label>
                </section>
              ) : field.type === "select" ? (
                <select
                  id={field.key}
                  className="edit-input edit-select"
                  value={form[field.key]}
                  onChange={(e) => handleChange(field.key, Number(e.target.value))}
                >
                  <option value="">— Select —</option>
                  {(options[field.optionsKey] || []).map((opt) => (
                    <option key={opt[field.valueKey]} value={opt[field.valueKey]}>
                      {opt[field.labelKey]} (ID: {opt[field.valueKey]})
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  id={field.key}
                  type={field.type === "number" ? "number" : "text"}
                  className="edit-input"
                  value={form[field.key]}
                  onChange={(e) =>
                    handleChange(field.key, field.type === "number" ? Number(e.target.value) : e.target.value)
                  }
                />
              )}
            </section>
          ))}
        </section>

        {error && <p className="edit-error">{error}</p>}

        <button className="edit-save-btn" onClick={() => setShowConfirm(true)}>
          Save changes
        </button>
      </section>

      {/* Confirm popup */}
      {showConfirm && (
        <section className="edit-modal-overlay">
          <section className="edit-modal">
            <h2>Confirm changes</h2>
            <p>Are you sure you want to save these changes?</p>
            <section className="edit-modal-actions">
              <button className="edit-modal-cancel" onClick={() => setShowConfirm(false)} disabled={saving}>
                Cancel
              </button>
              <button className="edit-modal-confirm" onClick={handleSubmit} disabled={saving}>
                {saving ? "Saving…" : "Confirm"}
              </button>
            </section>
          </section>
        </section>
      )}
    </section>
  );
}
