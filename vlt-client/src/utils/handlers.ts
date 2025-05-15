import React from "react";

export const handleCopyEvent = (e: React.MouseEvent<HTMLElement>, content: any) => {
    e.stopPropagation();
    navigator.clipboard
        .writeText(content)
        .then()
        .catch(err => {
            console.error("Failed to write into clipboard:", err);
        });
};