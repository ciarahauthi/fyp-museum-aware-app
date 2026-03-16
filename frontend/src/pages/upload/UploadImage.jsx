import { useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { imagesService } from "../../services/images";
import "./UploadImage.css";

export default function UploadImage() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const { folder, returnPath = "/manage-content" } = state || {};

    const fileInputRef = useRef(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [uploaded, setUploaded] = useState(null);
    const [error, setError] = useState(null);

    if (!folder) {
        return <p className="upload-error">No folder specified.</p>;
    }

    const handleFileChange = (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setSelectedFile(file);
        setPreview(URL.createObjectURL(file));
        setUploaded(null);
        setError(null);
    };

    const handleUpload = async () => {
        if (!selectedFile) return;
        setUploading(true);
        setError(null);
        try {
            const result = await imagesService.upload(folder, selectedFile);
            setUploaded(result);
            setSelectedFile(null);
            setPreview(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setUploading(false);
        }
    };

    return (
        <section className="upload-page">
            <header className="upload-header">
                <button
                    className="upload-back-btn"
                    onClick={() => navigate(returnPath)}
                >
                    ← Back
                </button>
                <h1 className="upload-title">Upload {folder} image</h1>
            </header>

            <section className="upload-body">
                <section
                    className="upload-dropzone"
                    onClick={() => fileInputRef.current?.click()}
                >
                    <p>Click to select an image</p>
                    <button className="upload-choose-btn" type="button">
                        Choose file
                    </button>
                    <input
                        ref={fileInputRef}
                        type="file"
                        accept="image/jpeg,image/png"
                        className="upload-file-input"
                        onChange={handleFileChange}
                    />
                </section>

                {preview && (
                    <section className="upload-preview">
                        <img src={preview} alt="Preview" />
                        <p>{selectedFile?.name}</p>
                    </section>
                )}

                {error && <p className="upload-error">{error}</p>}

                {uploaded && (
                    <section className="upload-success">
                        <p>Uploaded successfully!</p>
                        <code>{uploaded.url}</code>
                    </section>
                )}

                <button
                    className="upload-submit-btn"
                    onClick={handleUpload}
                    disabled={!selectedFile || uploading}
                >
                    {uploading ? "Uploading…" : "Upload image"}
                </button>
            </section>
        </section>
    );
}
