import { Box, Heading, Text } from "@chakra-ui/react";
import NavBar from "../components/navbar/NavBar";
import { isRouteErrorResponse, useRouteError } from "react-router-dom";


const ErrorPage = () => {

    const error = useRouteError();

    return (
        
        <>
        <NavBar/>
        <Box>
            <Heading>Ooops....</Heading>
            <Text fontSize="2xl" marginTop={4}>
                {isRouteErrorResponse(error) ? "Page not found" : "Something went wrong"}
            </Text>
        </Box>
        </>
    )

 }

export default ErrorPage;