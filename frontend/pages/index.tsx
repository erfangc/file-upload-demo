import React, {useState} from "react";
import axios from 'axios';

export default function Home() {

    const [selectedFile, setSelectedFile] = useState<File>()

    const [serverResponse, setServerResponse] = useState<any>()

    async function onSubmit() {
        if (!selectedFile) {
            return;
        }

        const formData = new FormData();

        formData.append(
            "file",
            selectedFile,
            selectedFile.name
        );

        const {data} = await axios.post("http://localhost:8080/upload", formData);

        setServerResponse(data);
    }

    function onFileChange(event: React.ChangeEvent<HTMLInputElement>) {
        if (!event.currentTarget.files || event.currentTarget.files.length < 1) {
            return;
        }
        setSelectedFile(event.currentTarget.files[0]);
    }

    return (
        <main style={{width: '50%', margin: 'auto', paddingTop: '24rem'}}>
            <div>
                <input type="file" name="file" onChange={onFileChange}/>
                <button onClick={onSubmit}>Submit</button>
            </div>
            <hr/>
            <h4>Server Response:</h4>
            <code>{JSON.stringify(serverResponse)}</code>
        </main>
    );
}
