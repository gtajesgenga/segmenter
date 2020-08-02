import React, {useState} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Button from "react-bootstrap/Button";
import Modal from 'react-bootstrap/Modal'

export function MyModal (props) {
    const [show, setShow] = useState(false);
    const {btnIcon, inputs, customClass, title, btnLabel} = props;

    const icon = btnIcon !== undefined ? <FontAwesomeIcon icon={btnIcon} size={'sm'}/> : <></>;

    return (
        <>
            <div className={customClass}>
                <Button className={'float-right'} variant={'success'} size={'sm'} onClick={() => setShow(true)}>
                    {icon}&nbsp;{btnLabel}
                </Button>

                <Modal show={show} onHide={() => setShow(false)}>
                    <Modal.Header closeButton>
                        <Modal.Title>{title}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {inputs}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant={'primary'} onClick={(e) => { setShow(false); props.callback(e) }}>{btnLabel}</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        </>
    )
}
