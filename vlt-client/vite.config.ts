import {ConfigEnv, defineConfig, loadEnv} from "vite";
import react from "@vitejs/plugin-react-swc";
import svgr from "vite-plugin-svgr";
import path from "node:path";

export default ({mode}: ConfigEnv) => {
    process.env = {...process.env, ...loadEnv(mode, process.cwd())};

    return defineConfig({
        plugins: [
            react({
                tsDecorators: true,
                plugins: [["@swc/plugin-styled-components", {}]]
            }),
            svgr({
                svgrOptions: {
                    icon: true,
                },
            }),
        ],
        resolve: {
            alias: {
                '@': path.resolve(__dirname, 'src'),
            }
        },
        base: "/",
        server: {
            open: false,
            port: Number(process.env.VITE_PORT),
            strictPort: true,
            proxy: {
                "/api": {
                    target: `http://${process.env.VITE_BACKEND_HOST}:${process.env.VITE_BACKEND_PORT}`,
                    changeOrigin: true,
                },
            },
        },
    });
}