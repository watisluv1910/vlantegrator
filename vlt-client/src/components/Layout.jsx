export const Layout = (props) => {
    const {children} = props;

    return (
        <Container>
            <NavBar>
                <NavButtons>
                    {
                        navItems.map((item) => (
                            <NavButton key={item.text} onClick={item.action}>
                                {item.text}
                            </NavButton>
                        ))
                    }
                </NavButtons>
            </NavBar>
            <Main>{children}</Main>
        </Container>
    );
};