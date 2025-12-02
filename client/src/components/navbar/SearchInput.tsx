import { Input, InputGroup, InputLeftElement } from '@chakra-ui/react';
import { BsSearch } from 'react-icons/bs';
import { useRef } from 'react';
import useGameQueryStore from '../../state';




export const SearchInput = () => {

    const ref = useRef<HTMLInputElement>(null);
    const onSearch = useGameQueryStore((state) => state.setSearchText);

    return (
        <form
            onSubmit={(event) => {
                event.preventDefault();
                onSearch(ref.current?.value || '');
            }}
        >
            <InputGroup>
                <InputLeftElement
                    children={<BsSearch />}
                />
                <Input
                    ref={ref}
                    borderRadius={20}
                    placeholder='Search movies...'
                    variant={"filled"}
                />
            </InputGroup>
        </form>
    );
};

export default SearchInput;