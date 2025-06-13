import React from "react";
import {Box, Link, Typography, Paper, List, ListItem, ListItemText} from "@mui/material";
import {Header} from "@/components/Header.tsx";
import {Sidebar} from "@/components/Sidebar.tsx";
import {MainSidebarContext} from "@/hooks/sidebarContexts.tsx";
import {DOCS} from "@/utils/constants.tsx";

export const DocsPage: React.FC = () => {
    const [sidebarWidth] = MainSidebarContext.useSidebarWidth();

    return (
        <>
            <Header currPath={["Документация"]} />
            <Sidebar />

            <Box
                sx={{
                    ml: `${sidebarWidth}px`,
                    transition: "margin .2s",
                    display: "flex",
                    justifyContent: "center",
                    p: 3,
                }}
            >
                <Paper sx={{ maxWidth: 600, width: "100%", p: 4 }}>
                    <Typography variant="h5" gutterBottom>
                        Документация платформы
                    </Typography>

                    <List>
                        {DOCS.map((doc) => (
                            <ListItem key={doc.url}>
                                <Link
                                    href={doc.url}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    underline="hover"
                                    variant="body1"
                                >
                                    <ListItemText primary={doc.label} />
                                </Link>
                            </ListItem>
                        ))}
                    </List>
                </Paper>
            </Box>
        </>
    );
};
