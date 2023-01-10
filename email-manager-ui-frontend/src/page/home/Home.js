import React, {useCallback, useEffect, useState} from "react";
import {useWindowDimensions, request, showInfo, showError} from "../../util";
import {Button, Dropdown, Input, Menu, Space, Typography, notification} from "antd";
import {
    DeleteOutlined,
    FileTextFilled,
    FolderFilled,
    FolderOpenOutlined, FormOutlined, LeftOutlined, ReloadOutlined,
    StepBackwardOutlined
} from "@ant-design/icons";
import {DELETE, GET, SERVER_ADDRESS, UPDATE} from "../../Information";
import {login} from "../components/Login";
import {displayMail} from "../components/MailDisplay";
import MailWriteModal from "../components/MailWriteModal";

const {Text, Paragraph, Title} = Typography;
const {Search} = Input;

const FileItem = ({name, icon, runed, contextMenuBehaviour, backgroundColor, draggable, other}) => {

    const menuBehaviourMap = contextMenuBehaviour.reduce((prev, now) => {
        prev[now.key] = now.behaviour;
        return prev;
    }, {});

    const contextMenu = (
        <Menu
            items={contextMenuBehaviour.map((item, _) => {
                const {key, label} = item;
                if (item.icon != null) {
                    return {key, label, icon: item.icon};
                } else {
                    return {key, label};
                }
            })}
            onClick={(item) => {
                menuBehaviourMap[item.key]();
            }}
        />
    );

    return (
        <Dropdown overlay={contextMenu} trigger={["contextMenu"]}>
            <div
                style={{
                    width: 150,
                    paddingTop: 4,
                    paddingBottom: 4,
                    margin: 0,
                    backgroundColor
                }}
                draggable={draggable}
                onDoubleClick={runed}
                {...other}>
                <div align={"center"}>
                    {icon}
                </div>
                <div align="center" style={{paddingLeft: 4, paddingRight: 4, marginTop: "4px"}}>
                    <Input value={name} disabled={true}
                           style={{color: "black", fontSize: "12px", textAlign: "center", cursor: "text"}}/>
                </div>
            </div>
        </Dropdown>
    );
};


export const Home = () => {
    const [folderPath, setFolderPath] = useState("");

    const [files, setFiles] = useState([]);

    const [onWriting, setOnWriting] = useState(false);

    const [searchKeyword, setSearchKeyword] = useState("");

    let fileDragging = null;

    const clearFiles = () => {
        setFiles([]);
    }

    const refreshFiles = async () => {
        try {
            let content = await request("/list", GET, {});
            setFolderPath(content.folderPath);
            setFiles(content.children);
        } catch (err) {
            showError(err.name, err.message);
        }
    };

    const refreshBackend = async () => {
        try {
            clearFiles();
            await request("/refresh", UPDATE, {});
            await refreshFiles();
        } catch (err) {
            showError(err.name, err.message);
        }
    }

    const showMessage = async (id) => {
        try {
            let mail = await request(`/mail/${id}`, GET, {});
            displayMail(mail);
        } catch (err) {
            showError(err.name, err.message);
        }
    };

    const removeMessage = async (id) => {
        try {
            clearFiles();
            await request(`/mail/${id}`, DELETE, {});
            await refreshFiles();
        } catch (err) {
            showError(err.name, err.message);
        }
    };

    const moveMessage = async (id, folder) => {
        try {
            clearFiles();
            await request(`/mail/move/${id}`, UPDATE, {
                paths: [folder]
            });
            await refreshFiles();
        } catch (err) {
            showError(err.name, err.message);
        }
    };

    const enterParent = async () => {
        await enterFolder("..");
        await refreshFiles();
    };

    const enterFolder = async (id) => {
        try {
            clearFiles();
            await request("/account/cd/", UPDATE, {
                paths: [id]
            });
            await refreshFiles();
        } catch (err) {
            showError(err.name, err.message);
        }
    };

    const MessageFile = ({message}) => {
        const [backgroundColor, setBackgroundColor] = useState("rgba(0, 0, 0, 0)");

        return (
            <FileItem
                name={message.displayName}
                icon={<FileTextFilled style={{fontSize: 100, color: "greenyellow"}}/>}
                runed={() => {
                    showMessage(message.id);
                }}
                contextMenuBehaviour={[
                    {
                        key: "open",
                        label: "Open",
                        icon: <FolderOpenOutlined/>,
                        behaviour() {
                            showMessage(message.id);
                        }
                    },
                    {
                        key: "remove",
                        label: "Delete",
                        icon: <DeleteOutlined/>,
                        behaviour() {
                            removeMessage(message.id);
                        }
                    },
                    {
                        key: "toParent",
                        label: "Move To Parent",
                        icon: <LeftOutlined/>,
                        behaviour() {
                            moveMessage(message.id, "..");
                        }
                    }
                ]}
                backgroundColor={backgroundColor}
                draggable={true}
                other={{
                    onMouseEnter: () => {
                        setBackgroundColor("lightblue");
                    },
                    onMouseLeave: () => {
                        setBackgroundColor("rgba(0, 0, 0, 0)");
                    },
                    onDragStart: () => {
                        fileDragging = message.id;
                    }
                }}
            />
        );
    };

    const MessageFolder = ({folder}) => {
        const [backgroundColor, setBackgroundColor] = useState("rgba(0, 0, 0, 0)");

        return (
            <FileItem
                name={folder.displayName}
                icon={<FolderFilled style={{fontSize: 100, color: "#ffe9a1"}}/>}
                runed={() => {
                    enterFolder(folder.id);
                }}
                contextMenuBehaviour={[
                    {
                        key: "open",
                        label: "open",
                        icon: <FolderOpenOutlined/>,
                        behaviour: () => enterFolder(folder.id)
                    },
                ]}
                backgroundColor={backgroundColor}
                draggable={false}
                other={{
                    onMouseEnter: () => {
                        setBackgroundColor("lightblue");
                    },
                    onMouseLeave: () => {
                        setBackgroundColor("rgba(0, 0, 0, 0)");
                    },
                    onDragOver: (e) => {
                        e.preventDefault();
                    },
                    onDrop: (e) => {
                        e.preventDefault();
                        if (fileDragging != null) {
                            moveMessage(fileDragging, folder.id);
                        }
                    }
                }}
            />
        );
    };

    const typeMapper = (type) => {
        if (type === "MESSAGE") {
            return (file) => <MessageFile message={file}/>;
        } else if (type === "FOLDER") {
            return (file) => <MessageFolder folder={file}/>;
        } else {
            return null;
        }
    };

    useEffect(() => {
        // check if is connected
        // if it is connected, then refresh directly, otherwise login then refresh
        request("/logined", GET, {})
            .then((logined) => {
                if (logined) {
                    refreshFiles().then(() => {
                    });
                } else {
                    login(refreshFiles);
                }
            });
    }, []);

    return (
        <>
            <div style={{width: "100%", height: "8vh", display: "inline-flex", alignItems: "center"}} align={"center"}>
                <span style={{
                    width: "10%",
                    height: 50,
                    display: "inline-flex",
                    alignItems: "center",
                    justifyContent: "center",
                    paddingLeft: 3,
                    paddingRight: 3
                }}>
                    <Button
                        icon={<StepBackwardOutlined style={{fontSize: 30}}/>}
                        onClick={enterParent}
                    />
                    <Button
                        icon={<ReloadOutlined style={{fontSize: 26}}/>}
                        onClick={refreshBackend}
                    />
                    <Button
                        icon={<FormOutlined style={{fontSize: 30}}/>}
                        onClick={() => {
                            setOnWriting(true)
                        }}
                    />
                </span>
                <span style={{
                    width: "70%",
                    height: 50,
                    display: "inline-flex",
                    alignItems: "center",
                    justifyContent: "center",
                    paddingLeft: 3,
                    paddingRight: 3
                }}>
                    <Search
                        style={{width: "100%"}}
                        placeholder={"Searching name..."}
                        onSearch={(str) => {
                            setSearchKeyword(str);
                        }}
                    />
                </span>
                <span style={{
                    width: "20%", paddingLeft: "1.5%", paddingRight: "1.5%",
                    display: "inline-flex", alignItems: "center", justifyContent: "center"
                }}>
                    <Input value={folderPath} disabled={true} style={{cursor: "text"}}/>
                </span>
            </div>
            <div>
                <Space direction={"horizontal"} wrap={true} align={"start"} style={{overflowY: "auto", height: "80vh"}}>
                    {
                        files.filter(file => {
                            if (file.displayName === null) {
                                return false;
                            }
                            if (searchKeyword === "" || searchKeyword === undefined || searchKeyword === null) {
                                return true;
                            } else {
                                return file.displayName.toLowerCase().indexOf(searchKeyword) >= 0;
                            }
                        }).map((file, index) =>
                            typeMapper(file.type)(file)
                        )
                    }
                </Space>
            </div>
            <MailWriteModal visible={onWriting} setVisible={setOnWriting}/>
        </>
    );
};