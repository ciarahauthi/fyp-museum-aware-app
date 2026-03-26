import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getUser } from "../../services/auth";
import Header from "../../components/header/Header";
import ActionMenu from "../../components/action_menu/ActionMenu";
import ConfirmPopup from "../../components/confirm_popup/ConfirmPopup";
import { homeService } from "../../services/home";
import "./ManageHomeScreen.css";

const HorizontalScroll = ({ title, items = [], onEdit, onDelete, onAdd }) => {
    return (
        <section className="horizontal-scroll-section">
            <section className="horizontal-scroll-header">
                <h2>{title}</h2>
                <button className="btn" onClick={onAdd}>
                    Add
                </button>
            </section>

            <ul className="horizontal-scroll-list">
                {items.map((item) => (
                    <li key={item.id} className="horizontal-scroll-card">
                        <section className="horizontal-scroll-card-actions">
                            <ActionMenu
                                onEdit={() => onEdit(item)}
                                onDelete={() => onDelete(item)}
                            />
                        </section>
                        <p>{item.title}</p>
                    </li>
                ))}
            </ul>
        </section>
    );
};

export default function ManageHomeScreen() {
    const navigate = useNavigate();
    const [sections, setSections] = useState({ top: [], mid: [], bottom: [] });
    const [currentUser, setCurrentUser] = useState(null);
    const [deleteItem, setDeleteItem] = useState(null);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        getUser()
            .then((u) => setCurrentUser(u))
            .catch(() => {});
    }, []);

    useEffect(() => {
        homeService.getAll()
            .then((data) => {
                setSections({
                    top: data.filter((item) => item.section === "top"),
                    mid: data.filter((item) => item.section === "mid"),
                    bottom: data.filter((item) => item.section === "bottom"),
                });
            })
            .catch((err) => console.error("Failed to fetch home data:", err));
    }, []);

    const handleEdit = (item) => {
        navigate("/edit", { state: { item, tableType: "homescreen", returnPath: "/manage-home-screen" } });
    };

    const handleAdd = (section) => {
        navigate("/edit", { state: { tableType: "homescreen", returnPath: "/manage-home-screen", defaultValues: { section, active: true } } });
    };

    const handleDelete = async () => {
        if (!deleteItem) return;
        setDeleting(true);
        try {
            await homeService.delete(deleteItem.id);
            setSections((prev) => {
                const section = deleteItem.section;
                return { ...prev, [section]: prev[section].filter((i) => i.id !== deleteItem.id) };
            });
            setDeleteItem(null);
        } catch (err) {
            console.error("Delete failed:", err);
        } finally {
            setDeleting(false);
        }
    };

    return (
        <section className="manage-homescreen">
            <Header title="Manage Home Screen" currentUser={currentUser} />
            <button
                className="btn"
                onClick={() => navigate("/upload", { state: { folder: "home", returnPath: "/manage-home-screen" } })}
            >
                + Home Item image
            </button>
            <section className="manage-homescreen-body">
                <HorizontalScroll title="Top Section" items={sections.top} onEdit={handleEdit} onDelete={setDeleteItem} onAdd={() => handleAdd("top")} />
                <HorizontalScroll title="Mid Section" items={sections.mid} onEdit={handleEdit} onDelete={setDeleteItem} onAdd={() => handleAdd("mid")} />
                <HorizontalScroll title="Bottom Section" items={sections.bottom} onEdit={handleEdit} onDelete={setDeleteItem} onAdd={() => handleAdd("bottom")} />
            </section>

            {deleteItem && (
                <ConfirmPopup
                    message={`Are you sure you want to delete "${deleteItem.title}"? This cannot be undone.`}
                    confirmLabel="Delete"
                    saving={deleting}
                    onConfirm={handleDelete}
                    onCancel={() => setDeleteItem(null)}
                />
            )}
        </section>
    );
}
