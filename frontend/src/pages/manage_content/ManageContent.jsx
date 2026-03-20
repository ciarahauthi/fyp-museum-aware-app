import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./ManageContent.css";
import { categories, tableConfigs, services, deleteServices, TABLE_SINGULAR } from "./ManageContentData";
import { getUser } from "../../services/auth";
import Header from "../../components/header/Header";
import ConfirmPopup from "../../components/confirm_popup/ConfirmPopup";
import ActionMenu from "../../components/action_menu/ActionMenu";

const CategoryPill = ({ label, active, onClick }) => (
    <button
        onClick={onClick}
        className={`category-pill ${active ? "active" : ""}`}
    >
        {label}
    </button>
);

const LoadingState = () => (
    <section className="manage-content-loading">
        <section className="manage-content-spinner" />
    </section>
);

const ErrorState = ({ message }) => (
    <section className="manage-content-error">
        <p>Error loading data</p>
        <p className="manage-content-error-detail">{message}</p>
    </section>
);

const EmptyState = ({ tableName }) => (
    <section className="manage-content-empty">
        <p>No {tableName} found</p>
    </section>
);

// Reusable table component
const GenericTable = ({ data, loading, error, config, onEdit, onDelete }) => {
    if (!config) {
        return (
            <section className="manage-content-error">
                <p>Invalid table configuration</p>
            </section>
        );
    }

    if (loading) return <LoadingState />;
    if (error) return <ErrorState message={error} />;
    if (!data?.length) return <EmptyState tableName={config.tableName} />;

    return (
        <section className="manage-table">
            {data.map((item) => (
                <section key={item.id} className="manage-table-row">
                    {config.columns.map((col) => (
                        <section
                            key={col.key}
                            className={`manage-table-cell ${col.cellClassName || ""}`}
                        >
                            {col.render ? col.render(item) : item[col.key]}
                        </section>
                    ))}
                    <section className="manage-table-actions">
                        <ActionMenu
                            onEdit={() => onEdit(item)}
                            onDelete={() => onDelete(item)}
                        />
                    </section>
                </section>
            ))}
        </section>
    );
};

export default function ManageContent() {
    const navigate = useNavigate();
    const [activeCategory, setActiveCategory] = useState(null);
    const [tableData, setTableData] = useState({});
    const [loading, setLoading] = useState({});
    const [errors, setErrors] = useState({});

    const [currentUser, setCurrentUser] = useState(null);
    const [deleteItem, setDeleteItem] = useState(null);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        getUser()
            .then((u) => setCurrentUser(u))
            .catch(() => {});
    }, []);

    const fetchTableData = async (tableName) => {
        const key = tableName.toLowerCase();
        if (tableData[key]) return;

        setLoading((prev) => ({ ...prev, [key]: true }));
        setErrors((prev) => ({ ...prev, [key]: null }));

        try {
            if (!services[key]) {
                throw new Error(`Not yet implemented for ${tableName}`);
            }

            const data = await services[key]();
            setTableData((prev) => ({ ...prev, [key]: data }));
        } catch (err) {
            console.error(`Fetch error for ${tableName}:`, err);
            setErrors((prev) => ({ ...prev, [key]: err.message }));
        } finally {
            setLoading((prev) => ({ ...prev, [key]: false }));
        }
    };

    const handleCategoryClick = (category) => {
        if (activeCategory === category) return;
        setActiveCategory(category);
        fetchTableData(category);
    };

    const activeKey = activeCategory?.toLowerCase();
    const activeConfig = activeKey ? tableConfigs[activeKey] : null;

    const handleDelete = async () => {
        if (!deleteItem || !activeKey) return;
        setDeleting(true);
        try {
            await deleteServices[activeKey](deleteItem.id);
            setTableData((prev) => ({
                ...prev,
                [activeKey]: prev[activeKey].filter((i) => i.id !== deleteItem.id),
            }));
            setDeleteItem(null);
        } catch (err) {
            console.error("Delete failed:", err);
        } finally {
            setDeleting(false);
        }
    };

    return (
        <section className="manage-content">
            <Header title="Manage Content" currentUser={currentUser} />
            <section className="manage-content-body">
                <section className="manage-content-pills">
                    {categories.map((cat) => (
                        <CategoryPill
                            key={cat}
                            label={cat}
                            active={false}
                            onClick={() => handleCategoryClick(cat)}
                        />
                    ))}

                    {activeCategory && (
                        <button
                            className="add-content-btn"
                            onClick={() =>
                                navigate("/edit", {
                                    state: { tableType: activeKey },
                                })
                            }
                        >
                            Add {activeCategory}
                        </button>
                    )}

                    <section className="manage-content-image-btns">
                        <button
                            className="btn"
                            onClick={() =>
                                navigate("/upload", {
                                    state: { folder: "exhibits" },
                                })
                            }
                        >
                            + Exhibit image
                        </button>
                        <button
                            className="btn"
                            onClick={() =>
                                navigate("/upload", {
                                    state: { folder: "routes" },
                                })
                            }
                        >
                            + Route image
                        </button>
                    </section>
                </section>

                {activeCategory ? (
                    <GenericTable
                        data={tableData[activeKey]}
                        loading={loading[activeKey]}
                        error={errors[activeKey]}
                        config={activeConfig}
                        onEdit={(item) =>
                            navigate("/edit", {
                                state: { item, tableType: activeKey },
                            })
                        }
                        onDelete={(item) => setDeleteItem(item)}
                    />
                ) : (
                    <section className="manage-content-empty">
                        Select a category to view its data
                    </section>
                )}

                {deleteItem && (
                    <ConfirmPopup
                        message={`Are you sure you want to delete this ${TABLE_SINGULAR[activeKey] ?? "item"}? This cannot be undone.`}
                        confirmLabel="Delete"
                        saving={deleting}
                        onConfirm={handleDelete}
                        onCancel={() => setDeleteItem(null)}
                    />
                )}
            </section>
        </section>
    );
}
