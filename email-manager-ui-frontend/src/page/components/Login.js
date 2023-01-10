import {Input, Modal, Select} from "antd";
import {request, showError} from "../../util";
import {GET, POST} from "../../Information";

const servers = [
    {
        key: "gmail",
        label: "Gmail",
        imapServer: "imap.gmail.com",
        imapPort: 993,
        smtpServer: "smtp.gmail.com",
        smtpPort: 587
    }
];

const eventValueSetter = (setter) => (e => setter(e.target.value));

const eventIntSetter = (setter) => (e => setter(parseInt(e.target.value)));

const showLoginPanel = (handleLogin) => {

    let imapServer = "",
        imapPort = 0,
        smtpServer = "",
        smtpPort = "",
        username = "",
        password = "";

    const tryLogin = () => {
        handleLogin(imapServer, imapPort, smtpServer, smtpPort, username, password);
    };

    Modal.confirm({
        title: "Login",
        content: (
            <div>
                <Input type={"text"} placeholder={"IMAP Server"}
                       onChange={eventValueSetter(v => imapServer = v)}/>
                <Input type={"number"} placeholder={"IMAP Port"}
                       onChange={eventIntSetter(v => imapPort = v)}/>
                <Input type={"text"} placeholder={"SMTP Server"}
                       onChange={eventValueSetter(v => smtpServer = v)}/>
                <Input type={"number"} placeholder={"SMTP Port"}
                       onChange={eventIntSetter(v => smtpPort = v)}/>
                <Input type={"text"} placeholder={"Username"}
                       onChange={eventValueSetter(v => username = v)}/>
                <Input.Password placeholder={"Password"}
                                onChange={eventValueSetter(v => password = v)}/>
            </div>
        ),
        okText: "Login",
        onOk: tryLogin
    });
};

export const login = (afterThat) => {
    Modal.destroyAll();
    showLoginPanel((imapServer, imapPort, smtpServer, smtpPort, username, password) => {
        request('/login', POST, {
            imapServer,
            imapPort,
            smtpServer,
            smtpPort,
            username,
            password
        }).then((data) => {
            afterThat();
        }).catch(err => {
            showError(err);
            login(afterThat);
        });
    });
};