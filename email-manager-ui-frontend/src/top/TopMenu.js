import {Menu} from "antd";
import "./TopMenu.css";
import {Link} from "react-router-dom";
import {HomeOutlined} from "@ant-design/icons";
import React from "react";
import {useNavigate} from "react-router-dom";

const menuItems = [
    {
        key: "home",
        label: "Home",
        title: "Home",
        icon: <HomeOutlined/>,
    }
];

const keyPathMap = {
    "home": "/"
};


export const TopMenu = () => {
    const history = useNavigate();
    return <>
        <div className="logo">
            Logo
        </div>
        <Menu theme="dark"
              mode="horizontal"
              items={menuItems}
              onClick={(key) => {
                  history.push(keyPathMap[key]);
              }}
        >
        </Menu>
    </>;
};