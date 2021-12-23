
import type { NextPage } from 'next'
import Button from '@mui/material/Button'
import { TestApi } from '@markovda/fn-api'

const TestPage : NextPage = () => {

    const onButtonClicked = () => {
        TestApi.hello().then((response) => {
            console.log(response.data)
        })
    }

    return (
        <div>
            <Button variant='contained' onClick={onButtonClicked}>Hello, Material UI!</Button>
        </div>
    )
}

export default TestPage;