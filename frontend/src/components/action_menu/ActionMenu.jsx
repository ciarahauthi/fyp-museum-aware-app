import { useState } from "react";
import { MdMoreHoriz } from "react-icons/md";
import "./ActionMenu.css";

export default function ActionMenu({ onEdit, onDelete }) {
    const [open, setOpen] = useState(false);

    return (
        <section className="action-menu">
            <button className="action-menu-btn" onClick={() => setOpen((o) => !o)}>
                <MdMoreHoriz size={20} />
            </button>
            {open && (
                <section className="action-menu-dropdown">
                    <button
                        className="action-menu-item"
                        onClick={() => { setOpen(false); onEdit(); }}
                    >
                        Edit entry
                    </button>
                    {onDelete && (
                        <button
                            className="action-menu-item action-menu-item--danger"
                            onClick={() => { setOpen(false); onDelete(); }}
                        >
                            Delete
                        </button>
                    )}
                </section>
            )}
        </section>
    );
}
