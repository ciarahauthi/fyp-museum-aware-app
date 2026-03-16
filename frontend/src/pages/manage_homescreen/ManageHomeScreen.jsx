import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getUser } from "../../services/auth";
import Header from "../../components/header/Header";
import { homeService } from "../../services/home";
import "./ManageHomeScreen.css";

const HorizontalScroll = ({ title, items = [], onEdit, onAdd }) => {
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
                    <p>{item.title}</p>
                    <button className="btn" onClick={() => onEdit(item)}>Edit</button>
                </li>
            ))}
        </ul>
    </section>
  );
};

export default function ManageHomeScreen() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState({});
    const [errors, setErrors] = useState({});
    const [sections, setSections] = useState({ top: [], mid: [], bottom: [] });

    const [currentUser, setCurrentUser] = useState(null);

    const handleEdit = (item) => {
        navigate("/edit", { state: { item, tableType: "homescreen", returnPath: "/manage-home-screen" } });
    };

    const handleAdd = (section) => {
        navigate("/edit", { state: { tableType: "homescreen", returnPath: "/manage-home-screen", defaultValues: { section, active: true } } });
    };
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
                bottom: data.filter((item) => item.section === "bottom")
            });
        })
        .catch((err) => console.error("Failed to fetch home data:", err));
    }, []);

    return (
        <section className="manage-homescreen">
            <Header title="Manage Home Screen" currentUser={currentUser} />
            <button 
                className="btn" 
                onClick={() =>
                    navigate("/upload", {
                        state: { folder: "home", returnPath: "/manage-home-screen" },
                    })
                }
            >
                + Home Item image
            </button>
            <section className="manage-homescreen-body">
                <HorizontalScroll title="Top Section" items={sections.top} onEdit={handleEdit} onAdd={() => handleAdd("top")}/>

                <HorizontalScroll title="Mid Section" items={sections.mid} onEdit={handleEdit} onAdd={() => handleAdd("mid")}/>

                <HorizontalScroll title="Bottom Section" items={sections.bottom} onEdit={handleEdit} onAdd={() => handleAdd("bottom")}/>

            </section>
        </section>
    );
}
