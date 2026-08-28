import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Composer } from './App.jsx'

describe('Composer', () => {
  it('shows the 250 character boundary', () => {
    render(<Composer onSubmit={() => {}} />)
    fireEvent.change(screen.getByLabelText('Text'), { target: { value: 'x'.repeat(250) } })
    expect(screen.getByText('250/250')).toBeInTheDocument()
  })
})

