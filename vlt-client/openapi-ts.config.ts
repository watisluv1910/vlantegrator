import {defaultPlugins, defineConfig} from "@hey-api/openapi-ts";

export default defineConfig({
    input: "../openapi/integrator-api.yaml",
    output: {
        lint: "eslint",
        format: "prettier",
        path: "src/api",
    },
    plugins: [
        ...defaultPlugins,
        {
            name: "@hey-api/client-axios",
            throwOnError: true,
            baseUrl: false,
        },
        {
            name: "@hey-api/typescript",
            exportInlineEnums: true,
        },
        {
            name: "@hey-api/sdk",
            serviceNameBuilder: "{{name}}Service",
            asClass: true,
        },
    ],
});