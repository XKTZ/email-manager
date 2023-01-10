import {FileAddOutlined} from "@ant-design/icons";
import {Button, Input, Modal} from "antd";

const HTML = "HTML";
const IMAGE = "IMAGE";
const COMBINATION = "COMBINATION";
const ATTACHMENT = "ATTACHMENT";
const COLLECTION = "COLLECTION";

const MailElement = ({element}) => {
    switch (element.type) {
        case HTML:
            return (
                <div dangerouslySetInnerHTML={{__html: element.html}}/>
            );
        case IMAGE:
            return (<></>);
        case COMBINATION:
        case COLLECTION:
            return (<>
                {element.elements.map(child => <MailElement element={child}/>)}
            </>);
        case ATTACHMENT:
            return (
                <div style={{display: "inline-flex", alignItems: "center"}} align={"center"}>
                    <FileAddOutlined style={{fontSize: 30, float: "left"}}/>
                    <Button style={{float: "right"}} onClick={() => {
                        console.log(element.data);
                    }}>Download</Button>
                </div>
            );
        default:
            return <></>;
    }
};

const MailDisplayModal = ({mail}) => {
    const {from, to} = mail.message;
    return (
        <div>
            <div>
                <div>
                    From: <Input value={from.join(', ')} disabled={true} style={{cursor: "text"}}/>
                    To: <Input value={to.join(', ')} disabled={true} style={{cursor: "text"}}/>
                </div>
            </div>
            <div style={{height: "55vh", overflowY: "auto", marginTop: 10}}>
                <MailElement element={mail.content}/>
            </div>
        </div>
    );
};

export const displayMail = (mail) => {
    const {displayName: subject} = mail.message;
    Modal.info({
        title: subject == null ? "" : subject,
        content: <MailDisplayModal mail={mail}/>,
        autoFocusButton: null,
        width: 800,
        style: {
            top: 10
        }
    });
};