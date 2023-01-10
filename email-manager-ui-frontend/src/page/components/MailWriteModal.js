import {useState} from "react";
import {Button, Input, Modal} from "antd";
import {request, showError, showInfo, usingIn} from "../../util";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.core.css"
import RichTextEditor from "./RichTextEditor";
import {DELETE, POST, UPDATE} from "../../Information";

const sendMail = async (subject, to, content) => {
    try {
        const toUsers = to.split(',').map(s => s.trim())
        const id = await request("/draft", POST, {
            header: {
                from: null, // default from
                to: toUsers,
                subject: subject
            },
            content: {
                root: {
                    html: content,
                    text: null,
                    type: "HTML"
                }
            }
        })
        await request(`/draft/send/${id}`, UPDATE, {})
        await request(`/draft/${id}`, DELETE, {})
        showInfo("Email Sent")
    } catch (err) {
        showError(err.name, err.message)
    }
}


export default ({visible, setVisible}) => {
    const [subject, setSubject] = useState("");
    const [to, setTo] = useState("");
    const [content, setContent] = useState("");

    const close = () => {
        setSubject("");
        setTo("");
        setContent("");
        setVisible(false);
    }
    return <Modal
        title={"Send Email"}
        visible={visible}
        width={1000}
        style={{
            top: "3vh"
        }}
        bodyStyle={{
            height: "75vh",
            paddingLeft: 20,
            paddingRight: 20,
            paddingTop: 10,
            paddingBottom: 0
        }}
        footer={[
            <Button key={"Cancel"} onClick={() => {
                close();
            }}>
                Cancel
            </Button>,
            <Button key={"Send"} onClick={() => {
                sendMail(subject, to, content);
                close();
            }}>
                Send
            </Button>
        ]}>
        <div style={{height: "10vh"}}>
            Subject:
            <Input value={subject} onChange={usingIn(e => e.target.value, setSubject)}/>
        </div>
        <div style={{height: "10vh"}}>
            To:
            <Input value={to} onChange={usingIn(e => e.target.value, setTo)}/>
        </div>
        <div>
            <RichTextEditor
                content={content}
                setContent={setContent}
                style={{
                    height: "45vh"
                }}
            />
        </div>
    </Modal>
}