import { Link, useNavigate, useLocation } from "react-router-dom";
import "./Sidebar.css";
import { SidebarData } from "./SidebarData";

const Sidebar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        navigate("/login");
    };

    return (
        <section className="sidebar">
            <ul>
                {SidebarData.map((item, index) => {
                    if (item.title === "Logout") {
                        return (
                            <li
                                key={index}
                                className="sidebar-option"
                                onClick={handleLogout}
                            >
                                <section className="sidebar-link">
                                    <section className="icon">
                                        {item.icon}
                                    </section>
                                    <section className="title">
                                        {item.title}
                                    </section>
                                </section>
                            </li>
                        );
                    }

                    return (
                        <li
                            key={index}
                            className={`sidebar-option ${
                                location.pathname === item.link ? "active" : ""
                            }`}
                        >
                            <Link to={item.link} className="sidebar-link">
                                <section className="icon">{item.icon}</section>
                                <section className="title">
                                    {item.title}
                                </section>
                            </Link>
                        </li>
                    );
                })}
            </ul>
        </section>
    );
};

export default Sidebar;
