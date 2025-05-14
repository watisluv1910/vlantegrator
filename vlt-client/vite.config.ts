import {defineConfig, loadEnv} from 'vite';
import react from '@vitejs/plugin-react-swc';
import svgr from 'vite-plugin-svgr';

// https://vitejs.dev/config/
export default defineConfig(({command, mode}) => {
    const env = loadEnv(mode, process.cwd());

    const commonConfig = {
        plugins: [
            react({
                tsDecorators: true,
                plugins: [['@swc/plugin-styled-components', {}]]
            }),
            svgr({
                svgrOptions: {
                    icon: true,
                },
            }),
        ],
        base: '/',
    };

    if (command === 'serve') {
        return {
            ...commonConfig,
            server: {
                open: false,
                port: Number(env.VITE_PORT),
                strictPort: true,
                proxy: {
                    '/api': {
                        target: `http://${env.BACKEND_HOST}:${env.BACKEND_PORT}`,
                        changeOrigin: true,
                    },
                },
            },
        };
    }

    return {
        ...commonConfig,
    };
});