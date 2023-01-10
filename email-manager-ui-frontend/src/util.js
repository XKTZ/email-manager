import {useEffect, useState} from "react";
import axios from "axios";
import {notification} from "antd";
import qs from "qs";

const getWindowDimension = () => {
    const {innerWidth: width, innerHeight: height} = window;
    return {width, height};
};

export const useWindowDimensions = () => {

    const [windowDimension, setWindowDimension] = useState(getWindowDimension);
    useEffect(() => {
        const handleResize = () => setWindowDimension(getWindowDimension());
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return windowDimension;
};

export const request = (addr, method, content) => {
    return new Promise((resolve, reject) => {
        axios({
            method,
            url: addr,
            data: content
        }).then(resp => {
            let data = resp.data;
            if (data.status === "SUCCESS") {
                resolve(data.content);
            } else {
                reject({
                    name: "ERROR",
                    message: data.content
                });
            }
        }).catch((err) => {
            reject(err);
        });
    });
};

export const showInfo = (title, msg) => {
    notification.info({
        message: title,
        description: msg
    });
};

export const showError = (title, msg) => {
    notification.error({
        message: title,
        description: msg
    });
};

export const usingIn = (mapping, func) => {
    return (value) => func(mapping(value));
}