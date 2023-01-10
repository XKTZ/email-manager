import {Button, Input, Layout, Space} from "antd";
import {TopMenu} from "./top/TopMenu";
import {Route, Routes} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {Home} from "./page/home/Home";


const {Header, Content} = Layout;

export const App = () => {
    return <Layout style={{height: "100vh"}}>
        <Header>
            <TopMenu/>
        </Header>
        <Content>
            <Routes>
                <Route path="/" element={<Home/>}/>
            </Routes>
        </Content>
    </Layout>;
};
