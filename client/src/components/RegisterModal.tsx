import React, { useState } from "react";
import {
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  FormControl,
  FormLabel,
  Input,
  useToast,
} from "@chakra-ui/react";
import { useAuth } from "../services/auth";

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

const RegisterModal: React.FC<Props> = ({ isOpen, onClose }) => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const auth = useAuth();

  const submit = async () => {
    if (password !== repeatPassword) {
      toast({
        title: "Passwords do not match",
        status: "error",
        duration: 3000,
      });
      return;
    }

    setLoading(true);

    try {
      await auth.register({ username, email, password });

      toast({ title: "Account created", status: "success", duration: 2000 });

      onClose();
    } catch (err: any) {
      toast({
        title: "Registration failed",
        description: err?.response?.data?.message ?? err?.message ?? "Unable to register",
        status: "error",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Create account</ModalHeader>
        <ModalBody>

          <FormControl mb={3}>
            <FormLabel>Username</FormLabel>
            <Input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </FormControl>

          <FormControl mb={3}>
            <FormLabel>Email</FormLabel>
            <Input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </FormControl>

          <FormControl mb={3}>
            <FormLabel>Password</FormLabel>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </FormControl>

          <FormControl mb={3}>
            <FormLabel>Repeat Password</FormLabel>
            <Input
              type="password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
            />
          </FormControl>

        </ModalBody>

        <ModalFooter>
          <Button mr={3} onClick={onClose}>
            Cancel
          </Button>
          <Button colorScheme="blue" isLoading={loading} onClick={submit}>
            Register
          </Button>
        </ModalFooter>

      </ModalContent>
    </Modal>
  );
};

export default RegisterModal;
