import "./Header.css";
export default function Header({ currentUser, title }) {
    return (
        <header className="manage-content-header">
            <h1>{title}</h1>
            {currentUser && (
                <section className="manage-content-user">
                    <section className="manage-content-user-info">
                        <p className="manage-content-user-name">
                            {currentUser.first_name} {currentUser.surname}
                        </p>
                    </section>
                    <section className="manage-content-avatar">
                        {currentUser.first_name.charAt(0)}
                    </section>
                </section>
            )}
        </header>
    );
}