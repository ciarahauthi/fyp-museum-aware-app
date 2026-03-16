import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { MdMoreHoriz } from "react-icons/md";
import "./ManageContent.css";
import { categories, tableConfigs, services } from "./ManageContentData";
import { getUser } from "../../services/auth";
import Header from "../../components/header/Header";

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
const GenericTable = ({ data, loading, error, config, onEdit }) => {
    const [openMenuId, setOpenMenuId] = useState(null);

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
                        <button
                            className="manage-table-action-btn"
                            onClick={() =>
                                setOpenMenuId(
                                    openMenuId === item.id ? null : item.id,
                                )
                            }
                        >
                            <MdMoreHoriz size={20} />
                        </button>
                        {openMenuId === item.id && (
                            <section className="manage-table-dropdown">
                                <button
                                    className="manage-table-dropdown-item"
                                    onClick={() => {
                                        setOpenMenuId(null);
                                        onEdit(item);
                                    }}
                                >
                                    Edit entry
                                </button>
                            </section>
                        )}
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
                    />
                ) : (
                    <section className="manage-content-empty">
                        Select a category to view its data
                    </section>
                )}
            </section>
        </section>
    );
}
