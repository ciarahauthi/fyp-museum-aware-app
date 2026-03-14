import "./ConfirmPopup.css";

export default function ConfirmPopup({ message, confirmLabel = "Confirm", saving, onConfirm, onCancel }) {
  return (
    <section className="confirm-popup-overlay">
      <section className="confirm-popup">
        <h2>Confirm</h2>
        <p>{message}</p>
        <section className="confirm-popup-actions">
          <button className="confirm-popup-cancel" onClick={onCancel} disabled={saving}>
            Cancel
          </button>
          <button className="confirm-popup-confirm" onClick={onConfirm} disabled={saving}>
            {saving ? "Please wait…" : confirmLabel}
          </button>
        </section>
      </section>
    </section>
  );
}
